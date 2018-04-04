package com.xj.common.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xj.b2b.back.entity.User;
import com.xj.b2b.back.utils.UserUtils;
import com.xj.common.config.Global;
import com.xj.common.utils.FileUtils;
import com.xj.common.utils.StringUtils;

public class UserfilesUploadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 保存文件的目录
	private static String PATH = Global.USERFILES_BASE_URL + Global.USERFILES_TEMP_URL;
	private static String PATH_FOLDER = Global.USERFILES_BASE_URL + Global.USERFILES_TEMP_URL;
	// 存放临时文件的目录
	private static String TEMP_FOLDER = Global.USERFILES_BASE_URL + "tmp/";

	@Override
	public void init(ServletConfig config) throws ServletException {
		// 初始化路径，兼容 Linux 系统
		String classPath = this.getClass().getClassLoader().getResource("/").getPath();
		String rootPath = classPath.substring(0, classPath.lastIndexOf("WEB-INF"));

		// System.out.println("1 PATH_FOLDER " + PATH_FOLDER);
		// System.out.println("1 TEMP_FOLDER " + TEMP_FOLDER);
		// System.out.println("1 classPath " + classPath);
		// System.out.println("1 rootPath " + rootPath);

		if (StringUtils.isNotBlank(rootPath)) {
			PATH_FOLDER = FileUtils.path(rootPath + PATH_FOLDER);
			TEMP_FOLDER = FileUtils.path(rootPath + TEMP_FOLDER);

			if (!PATH_FOLDER.endsWith(File.separator)) {
				PATH_FOLDER = PATH_FOLDER + File.separator;
			}
			if (!TEMP_FOLDER.endsWith(File.separator)) {
				TEMP_FOLDER = TEMP_FOLDER + File.separator;
			}

			// System.out.println("3 PATH_FOLDER " + PATH_FOLDER);
			// System.out.println("3 TEMP_FOLDER " + TEMP_FOLDER);
		}

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		User currUser = UserUtils.getUser();

		request.setCharacterEncoding("utf-8"); // 设置编码
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");

		// 获得磁盘文件条目工厂
		DiskFileItemFactory factory = new DiskFileItemFactory();

		// 如果没以下两行设置的话，上传大的 文件 会占用 很多内存，
		// 设置暂时存放的 存储室 , 这个存储室，可以和 最终存储文件 的目录不同
		/**
		 * 原理 它是先存到 暂时存储室，然后在真正写到 对应目录的硬盘上， 按理来说 当上传一个文件时，其实是上传了两份，第一个是以 .tem
		 * 格式的 然后再将其真正写到 对应目录的硬盘上
		 */
		factory.setRepository(new File(TEMP_FOLDER));
		// 设置 缓存的大小，当上传文件的容量超过该缓存时，直接放到 暂时存储室
		factory.setSizeThreshold(1024 * 1024);

		// 高水平的API文件上传处理
		ServletFileUpload upload = new ServletFileUpload(factory);

		try {
			// 提交上来的信息都在这个list里面
			// 这意味着可以上传多个文件
			// 请自行组织代码
			List<FileItem> list = upload.parseRequest(request);
			// 获取上传的文件
			FileItem item = getUploadFileItem(list);
			// 获取文件名
			String filename = getUploadFileName(item);
			// 保存后的文件名
			String saveName = currUser.getId() + "-" + new Date().getTime()
					+ filename.substring(filename.lastIndexOf("."));
			// 保存后图片的浏览器访问路径
			String fileUrl = PATH + saveName;

			// System.out.println("存放目录:" + PATH_FOLDER);
			// System.out.println("文件名:" + filename);
			// System.out.println("浏览器访问路径:" + fileUrl);

			File targetFile = new File(PATH_FOLDER, saveName);

			if (!targetFile.getParentFile().exists()) {
				targetFile.getParentFile().mkdirs();
			}

			// 真正写到磁盘上
			item.write(targetFile); // 第三方提供的

			PrintWriter writer = response.getWriter();

			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode objectNode = objectMapper.createObjectNode();
			objectNode.put("fileSize", item.getSize());
			objectNode.put("fileName", filename);
			objectNode.put("fileUrl", fileUrl);

			System.out.println("文件上传：" + objectNode.toString());

			writer.print(objectNode.toString());
			writer.close();

		} catch (FileUploadException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private FileItem getUploadFileItem(List<FileItem> list) {
		for (FileItem fileItem : list) {
			if (!fileItem.isFormField()) {
				return fileItem;
			}
		}
		return null;
	}

	private String getUploadFileName(FileItem item) {
		// 获取格式化路径名，兼容IE，Edge
		String value = FileUtils.path(item.getName());
		// 索引到最后一个反斜杠
		int start = value.lastIndexOf("/");
		// 截取 上传文件的 字符串名字，加1是 去掉反斜杠，
		String filename = value.substring(start + 1);

		return filename;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

}
