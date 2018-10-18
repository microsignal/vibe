package io.microvibe.booster.system.restful;

import io.microvibe.booster.commons.crypto.MessageDigestUtil;
import io.microvibe.booster.commons.utils.IOUtils;
import io.microvibe.booster.core.api.ApiException;
import io.microvibe.booster.core.api.model.ResponseData;
import io.microvibe.booster.core.api.tools.DataKit;
import io.microvibe.booster.core.base.annotation.BaseComponent;
import io.microvibe.booster.core.base.controller.BaseController;
import io.microvibe.booster.core.env.SystemEnv;
import io.microvibe.booster.system.entity.SysFile;
import io.microvibe.booster.system.enums.FileStatus;
import io.microvibe.booster.system.service.SysFileService;
import io.microvibe.booster.system.storage.StorageManager;
import io.microvibe.booster.system.storage.StorageMode;
import io.microvibe.booster.system.storage.Storages;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通用文件处理
 *
 * @author Q
 * @version 1.0
 * @since Jul 14, 2018
 */
@RestController
@RequestMapping(value = "/openapi/file")
public class OpenFileController extends BaseController<SysFile, String> {

	@Autowired
	@BaseComponent
	private SysFileService sysFileService;

	@Autowired
	private SystemEnv systemEnv;
	private Map<String, String> fileMIMETypes = new ConcurrentHashMap<>();

	public OpenFileController(){
		fileMIMETypes.put("jpg", "image/jpg");
		fileMIMETypes.put("jpeg", "image/jpeg");
		fileMIMETypes.put("png", "image/png");
		fileMIMETypes.put("ico", "image/x-icon");
		fileMIMETypes.put("gif", "image/gif");
		fileMIMETypes.put("tiff", "image/tiff");
		fileMIMETypes.put("tif", "image/tiff");
	}

	/**
	 * 文件上传, 成功后返回文件ID
	 * @param file
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	@RequestMapping("/upload")
	@ResponseBody
	public ResponseData upload(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {
		if (file == null || file.isEmpty()) {
			throw new ApiException("文件不存在或内容为空");
		}
		// 取得当前上传文件的文件名称
		String originalFilename = file.getOriginalFilename();
		String fileType = FilenameUtils.getExtension(originalFilename).toLowerCase();

		SysFile sysFile = new SysFile();
		StorageMode storageMode = StorageMode.local;
		if (systemEnv.isDevelopMode()) {
			storageMode = StorageMode.database;
			InputStream in = file.getInputStream();
			byte[] byteArray = IOUtils.toByteArray(in);
			String sha1 = MessageDigestUtil.sha1AsString(byteArray);
			sysFile.setFileHash(sha1);//hash
			sysFile.setFileContent(byteArray);//content
		} else {
			StorageManager manager = Storages.getManager(storageMode);
			if (manager == null) {
				throw new ApiException("文件的存储方式不支持!");
			}
			String filepath = new SimpleDateFormat("yyyy/MM/dd/HHmmss_SSS_").format(new Date());
			filepath += RandomStringUtils.randomAlphanumeric(6) + "." + fileType;

			InputStream in = file.getInputStream();
			byte[] byteArray = IOUtils.toByteArray(in);
			String sha1 = MessageDigestUtil.sha1AsString(byteArray);

			manager.output(filepath, byteArray);
			sysFile.setFileHash(sha1);//hash
			sysFile.setFilePath(filepath);//path
		}
		sysFile.setFileName(originalFilename);
		sysFile.setFileType(fileType);
		sysFile.setStorageMode(storageMode);
		sysFile.setStatus(FileStatus.temporary);// 临时态,与业务关联后变更状态为正常
		sysFileService.insert(sysFile);

		String id = sysFile.getId();
		ResponseData responseData = DataKit.buildSuccessResponse();
		responseData.setBody("fileId", id);
		return responseData;
	}


	/**
	 * 确定文件为业务用途的有效文件,状态由临时状态变更为正常
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	@RequestMapping("/confirm/{id}")
	@ResponseBody
	public ResponseData confirm(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {
		SysFile file = sysFileService.getById(id);
		if (file == null || file.deleted()) {
			return DataKit.buildErrorResponse("文件不存在!");
		}
		sysFileService.updateToNormalStatus(id);
		return DataKit.buildSuccessResponse();
	}

	/**
	 * 文件下载, 图片类型文件可直接显示于浏览器
	 * @param id
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	@RequestMapping("/{id}")
	@ResponseBody
	public void download(@PathVariable("id") String id, HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {
		SysFile file = sysFileService.getById(id);
		if (file == null || file.deleted()) {
			// 404
			throw new NoHandlerFoundException(request.getMethod(), request.getRequestURI(), new HttpHeaders());
		}
		InputStream in = null;

		StorageMode storageMode = file.getStorageMode();
		if (systemEnv.isDevelopMode() && storageMode == StorageMode.database) {
			byte[] fileContent = file.getFileContent();
			in = new ByteArrayInputStream(fileContent);
		} else {
			StorageManager manager = Storages.getManager(storageMode);
			if (manager == null) {
				throw new ApiException("文件的存储方式不支持!");
			}
			in = manager.input(file.getFilePath());
		}

		String filename = file.getFileName();
		String fileType = file.getFileType();

		response.reset();
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		String contentType = fileMIMETypes.get(fileType);//不同类型的文件对应不同的MIME类型
		if (contentType == null) {
			contentType = "application/x-" + fileType;
		}
		response.setContentType(contentType);
		if (!contentType.startsWith("image/")) {
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(filename, "UTF-8"));
		}
		OutputStream out = response.getOutputStream();
		IOUtils.copy(in, out);
		out.flush();
		out.close();
	}


}
