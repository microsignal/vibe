package io.microvibe.booster.system.auth;

import lombok.Getter;

public enum ResourceOperation {

	any("*"),//任意
	list, //列表
	export,//导出
	view, //明细
	create,//新增
	update,//修改
	delete,//删除
	upload,//上传
	download,//下载
	;

	@Getter
	private String code;

	private ResourceOperation() {
		this.code = this.name();
	}

	private ResourceOperation(String code) {
		this.code = code;
	}

	public String toPermission(String resource) {
//		return this.code + ":" + resource;
		return resource + ":" + this.code;
	}

}
