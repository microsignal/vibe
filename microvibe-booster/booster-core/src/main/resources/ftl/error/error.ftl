<style>
	.error-title {
		display: inline;
		cursor: pointer;
		-webkit-user-select: none;
		-moz-user-select: none;
		-ms-user-select: none;
		user-select: none;
	}

</style>
<h3 class="error-title" ondblclick="toggleErrorStackTrace()" title="双击查看错误堆栈">
${(error.status)!} 出错了！
</h3>
<div>
	<div>
		错误信息：${(error.message)!}
	</div>
	<div id="errorStackTraceDiv" style="display: none;font-size: xx-small;">
		<hr>
		<pre>${(error.trace)!}</pre>
	</div>
</div>
<script>
	function toggleErrorStackTrace(){
		var ele = document.getElementById('errorStackTraceDiv');
		ele.style.display = ({'none':'block','block':'none'})[ele.style.display]
	}
</script>
