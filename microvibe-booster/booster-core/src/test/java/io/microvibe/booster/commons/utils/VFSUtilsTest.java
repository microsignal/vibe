//package io.microvibe.booster.commons.utils;
//
//import com.jcraft.jsch.UserInfo;
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.apache.commons.vfs2.FileObject;
//import org.apache.commons.vfs2.FileSystemOptions;
//import org.apache.commons.vfs2.provider.sftp.IdentityInfo;
//import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
//import org.apache.commons.vfs2.provider.sftp.TrustEveryoneUserInfo;
//import org.apache.commons.vfs2.util.RandomAccessMode;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.OutputStream;
//
///**
// * @author Qt
// * @since Jul 14, 2018
// */
//public class VFSUtilsTest {
//
//	@Test
//	public void testFtp() throws IOException {
//		FileSystemOptions opts = new FileSystemOptions();
//		SftpFileSystemConfigBuilder builder = SftpFileSystemConfigBuilder.getInstance();
//		builder.setUserDirIsRoot(opts, false);
//		FileObject f = VFSUtils.getFileSystemManager().resolveFile("ftp://upload:upload@localhost/2018/test.txt", opts);
//		OutputStream out = f.getContent().getOutputStream();
//		IOUtils.write(RandomStringUtils.randomAlphanumeric(32)+"\n", out);
//		IOUtils.write(RandomStringUtils.randomAlphanumeric(32)+"\n", out);
//		IOUtils.write(RandomStringUtils.randomAlphanumeric(32)+"\n", out);
//		IOUtils.write(RandomStringUtils.randomAlphanumeric(32)+"\n", out);
//		out.flush();
//		out.close();
//	}
//
//	@Test
//	public void testSftp() throws IOException {
//		FileObject f = VFSUtils.getFileSystemManager().resolveFile(
//			"sftp://ubuntu@10.10.1.7:10086//home/ubuntu/upload/test.txt",
//			VFSUtils.buildSftpOpts(new File(System.getProperty("user.home") + "/.ssh/ubuntu_id_rsa")));
//		System.out.printf("content: \n%s\n", IOUtils.toString(f.getContent().getInputStream()));
//
//		OutputStream out = f.getContent().getOutputStream();
//		IOUtils.write(RandomStringUtils.randomAlphanumeric(32)+"\n", out);
//		IOUtils.write(RandomStringUtils.randomAlphanumeric(32)+"\n", out);
//		IOUtils.write(RandomStringUtils.randomAlphanumeric(32)+"\n", out);
//		IOUtils.write(RandomStringUtils.randomAlphanumeric(32)+"\n", out);
//
//		out.flush();
//		IOUtils.closeQuietly(out);
//		IOUtils.closeQuietly(f);
//	}
//
//}
