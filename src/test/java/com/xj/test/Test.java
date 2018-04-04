package com.xj.test;

import com.xj.common.config.Global;
import com.xj.common.security.Digests;
import com.xj.common.utils.Encodes;
import com.xj.common.utils.FileUtils;

public class Test {

	public static void main(String[] args) {

		System.out.println("\r\n--------------------------------------------------");
		// 生成明文对应的密码
		getHash("admin");

		System.out.println("\r\n--------------------------------------------------");
		// 模拟用户ID，获取多层次用户根路径
		getUserPath("1001");

	}

	/**
	 * 生成字符串的哈希值，可用于手动生成系统用户存储到数据库的密码字段
	 */
	public static void getHash(String plainPassword) {
		String plain = Encodes.unescapeHtml(plainPassword);
		byte[] salt = Digests.generateSalt(8);
		String hashPwd = Encodes.encodeHex(Digests.sha(plain.getBytes(), Digests.SHA256, salt, 1024));
		System.out.println("明文字符串  " + plain + " 对应的密文为 " + Encodes.encodeHex(salt) + hashPwd);

	}

	/**
	 * 获取制定用户ID的多层次用户根路径
	 */
	public static void getUserPath(String userId) {

		// 常用路径
		String serverPath = Global.getUserfilesBaseDir(); // 项目根路径
		String tempPath = Global.USERFILES_BASE_URL + Global.USERFILES_TEMP_URL; // 相对临时路径
		String userPath = Global.USERFILES_BASE_URL + Global.getUserfilesUrl(userId); // 相对用户根路径
		System.out.println("项目根路径: " + serverPath);
		System.out.println("公共临时路径: " + tempPath);
		System.out.println("用户根路径: " + userPath);

		String fileTmpPath = FileUtils.path(serverPath + tempPath + "文件名"); // 临时文件路径
		String filePath = FileUtils.path(serverPath + userPath + "子目录名/文件名"); // 实际文件路径
		System.out.println("公共临时文件路径: " + fileTmpPath);
		System.out.println("用户文件路径: " + filePath);

	}
}
