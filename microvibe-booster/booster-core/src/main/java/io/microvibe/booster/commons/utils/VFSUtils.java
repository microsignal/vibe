package io.microvibe.booster.commons.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.sftp.IdentityInfo;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

import java.io.*;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * 虚拟文件处理类,基于vfs进行二次封装，针对常用的，文件写入，文件读取，文件拷贝进行封装
 * <br>
 * <a href="http://commons.apache.org/vfs/filesystems.html">Supported File Systems:</a>
 * <br>
 * <div class="section">
 * <h2>
 * <a name="Supported_File_Systems"></a>Supported File Systems
 * </h2>
 * <p>
 * Commons VFS directly supports the following file systems with the
 * listed <a href="apidocs/org/apache/commons/vfs2/Capability.html">capabilities</a>:
 * </p>
 * <table border="1" style="border-collapse: collapse">
 * <tbody>
 * <tr class="a">
 * <th>File System</th>
 * <th>Directory Contents</th>
 * <th>Authentication</th>
 * <th>Read</th>
 * <th>Write</th>
 * <th>Create/Delete</th>
 * <th>Random</th>
 * <th>Version</th>
 * <th>Rename</th>
 * </tr>
 * <tr class="b">
 * <td><a href="#gzip_and_bzip2">BZIP2</a></td>
 * <td>No</td>
 * <td>No</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>No</td>
 * <td>No</td>
 * <td>No</td>
 * <td>No</td>
 * </tr>
 * <tr class="a">
 * <td><a href="#Local_Files">File</a></td>
 * <td>No</td>
 * <td>No</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Read/Write</td>
 * <td>No</td>
 * <td>Yes</td>
 * </tr>
 * <tr class="b">
 * <td><a href="#FTP">FTP</a></td>
 * <td>No</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Read</td>
 * <td>No</td>
 * <td>Yes</td>
 * </tr>
 * <tr class="a">
 * <td><a href="#FTPS">FTPS</a></td>
 * <td>No</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Read</td>
 * <td>No</td>
 * <td>Yes</td>
 * </tr>
 * <tr class="b">
 * <td><a href="#gzip_and_bzip2">GZIP</a></td>
 * <td>No</td>
 * <td>No</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>No</td>
 * <td>No</td>
 * <td>No</td>
 * <td>No</td>
 * </tr>
 * <tr class="a">
 * <td><a href="#HDFS">HDFS</a></td>
 * <td>Yes</td>
 * <td>No</td>
 * <td>Yes</td>
 * <td>No</td>
 * <td>No</td>
 * <td>Read</td>
 * <td>No</td>
 * <td>No</td>
 * </tr>
 * <tr class="b">
 * <td><a href="#HTTP_and_HTTPS">HTTP</a></td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>No</td>
 * <td>No</td>
 * <td>Read</td>
 * <td>No</td>
 * <td>No</td>
 * </tr>
 * <tr class="a">
 * <td><a href="#HTTP_and_HTTPS">HTTPS</a></td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>No</td>
 * <td>No</td>
 * <td>Read</td>
 * <td>No</td>
 * <td>No</td>
 * </tr>
 * <tr class="b">
 * <td><a href="#Zip_Jar_and_Tar">Jar</a></td>
 * <td>No</td>
 * <td>No</td>
 * <td>Yes</td>
 * <td>No</td>
 * <td>No</td>
 * <td>No</td>
 * <td>No</td>
 * <td>No</td>
 * </tr>
 * <tr class="a">
 * <td><a href="#ram">RAM</a></td>
 * <td>No</td>
 * <td>No</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Read/Write</td>
 * <td>No</td>
 * <td>Yes</td>
 * </tr>
 * <tr class="b">
 * <td><a href="#res">RES</a></td>
 * <td>No</td>
 * <td>No</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Read/Write</td>
 * <td>No</td>
 * <td>Yes</td>
 * </tr>
 * <tr class="a">
 * <td><a href="#SFTP">SFTP</a></td>
 * <td>No</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Read</td>
 * <td>No</td>
 * <td>Yes</td>
 * </tr>
 * <tr class="b">
 * <td><a href="#Zip_Jar_and_Tar">Tar</a></td>
 * <td>No</td>
 * <td>No</td>
 * <td>Yes</td>
 * <td>No</td>
 * <td>No</td>
 * <td>No</td>
 * <td>No</td>
 * <td>No</td>
 * </tr>
 * <tr class="a">
 * <td><a href="#Temporary_Fils">Temp</a></td>
 * <td>No</td>
 * <td>No</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Read/Write</td>
 * <td>No</td>
 * <td>Yes</td>
 * </tr>
 * <tr class="b">
 * <td><a href="WebDAV">WebDAV</a></td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Read/Write</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * </tr>
 * <tr class="a">
 * <td><a href="#Zip_Jar_and_Tar">Zip</a></td>
 * <td>No</td>
 * <td>No</td>
 * <td>Yes</td>
 * <td>No</td>
 * <td>No</td>
 * <td>No</td>
 * <td>No</td>
 * <td>No</td>
 * </tr>
 * </tbody>
 * </table>
 * </div>
 * <div class="section">
 * <h2>
 * <a name="Things_from_the_sandbox"></a>Things from the sandbox
 * </h2>
 * <p>The following file systems are in development:</p>
 * <table border="1" style="border-collapse: collapse">
 * <tbody>
 * <tr class="a">
 * <th>File System</th>
 * <th>Directory Contents</th>
 * <th>Authentication</th>
 * <th>Read</th>
 * <th>Write</th>
 * <th>Create/Delete</th>
 * <th>Random</th>
 * <th>Version</th>
 * <th>Rename</th>
 * </tr>
 * <tr class="b">
 * <td><a href="#CIFS">CIFS</a></td>
 * <td>No</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Yes</td>
 * <td>Read/Write</td>
 * <td>No</td>
 * <td>Yes</td>
 * </tr>
 * <tr class="a">
 * <td><a href="#mime">mime</a></td>
 * <td>No</td>
 * <td>No</td>
 * <td>Yes</td>
 * <td>No</td>
 * <td>No</td>
 * <td>No</td>
 * <td>No</td>
 * <td>No</td>
 * </tr>
 * </tbody>
 * </table>
 * </div>
 * <div class="section">
 * <h2>
 * <a name="Naming"></a>Naming
 * </h2>
 * <p>
 * All filenames are treated as URIs. One of the consequences of this is
 * you have to encode the '%' character using
 * <tt>%25</tt>
 * . <br> Depending on the filesystem additional characters are
 * encoded if needed. This is done automatically, but might be reflected
 * in the filename.
 * </p>
 * <p>
 * <b>Examples</b>
 * </p>
 * <ul>
 * <li><tt>file:///somedir/some%25file.txt</tt></li>
 * </ul>
 * <p>Many file systems accept a userid and password as part of the
 * url. However, storing a password in clear text in a file is usually
 * unacceptable. To help with that Commons VFS provides a mechanism to
 * encrypt the password. It should be noted though, that this is not
 * completely secure since the password needs to be unencrypted before
 * Commons VFS can use it.</p>
 * <p>To create an encrypted password do:</p>
 * <tt> java -cp commons-vfs-2.0.jar
 * org.apache.commons.vfs2.util.EncryptUtil encrypt mypassword </tt>
 * <p>
 * where <i>mypassword</i> is the password you want to encrypt. The
 * result of this will be a single line of output containing uppercase
 * hex characters. For example,
 * </p>
 * <tt> java -cp commons-vfs-2.0.jar
 * org.apache.commons.vfs2.util.EncryptUtil encrypt WontUBee9
 * D7B82198B272F5C93790FEB38A73C7B8 </tt>
 * <p>Then cut the output returned and paste it into the URL as:</p>
 * <tt>
 * https://testuser:{D7B82198B272F5C93790FEB38A73C7B8}@myhost.com/svn/repos/vfstest/trunk
 * </tt>
 * <p>VFS treats a password enclosed in {} as being encrypted and
 * will decrypt the password before using it.</p>
 * </div>
 * <div class="section">
 * <h2>
 * <a name="Local_Files"></a>Local Files
 * </h2>
 * <p>Provides access to the files on the local physical file system.</p>
 * <p>
 * <b>URI Format</b>
 * </p>
 * <p>
 * <tt>
 * [file://] <i>absolute-path</i>
 * </tt>
 * </p>
 * <p>
 * Where
 * <tt>
 * <i>absolute-path</i>
 * </tt>
 * is a valid absolute file name for the local platform. UNC names are
 * supported under Windows.
 * </p>
 * <p>
 * <b>Examples</b>
 * </p>
 * <ul>
 * <li><tt>file:///home/someuser/somedir</tt></li>
 * <li><tt>file:///C:/Documents and Settings</tt></li>
 * <li><tt>file://///somehost/someshare/afile.txt</tt></li>
 * <li><tt>/home/someuser/somedir</tt></li>
 * <li><tt>c:\program files\some dir</tt></li>
 * <li><tt>c:/program files/some dir</tt></li>
 * </ul>
 * </div>
 * <div class="section">
 * <h2>
 * <a name="Zip_Jar_and_Tar"></a>Zip, Jar and Tar
 * </h2>
 * <p>Provides read-only access to the contents of Zip, Jar and Tar
 * files.</p>
 * <p>
 * <b>URI Format</b>
 * </p>
 * <p>
 * <tt>
 * zip:// <i>arch-file-uri</i>[! <i>absolute-path</i>]
 * </tt>
 * </p>
 * <p>
 * <tt>
 * jar:// <i>arch-file-uri</i>[! <i>absolute-path</i>]
 * </tt>
 * </p>
 * <p>
 * <tt>
 * tar:// <i>arch-file-uri</i>[! <i>absolute-path</i>]
 * </tt>
 * </p>
 * <p>
 * <tt>
 * tgz:// <i>arch-file-uri</i>[! <i>absolute-path</i>]
 * </tt>
 * </p>
 * <p>
 * <tt>
 * tbz2:// <i>arch-file-uri</i>[! <i>absolute-path</i>]
 * </tt>
 * </p>
 * <p>
 * Where
 * <tt>arch-file-uri</tt>
 * refers to a file of any supported type, including other zip files.
 * Note: if you would like to use the ! as normal character it must be
 * escaped using
 * <tt>%21</tt>
 * .<br>
 * <tt>tgz</tt>
 * and
 * <tt>tbz2</tt>
 * are convenience for
 * <tt>tar:gz</tt>
 * and
 * <tt>tar:bz2</tt>
 * .
 * </p>
 * <p>
 * <b>Examples</b>
 * </p>
 * <ul>
 * <li><tt>jar:../lib/classes.jar!/META-INF/manifest.mf</tt></li>
 * <li><tt>zip:http://somehost/downloads/somefile.zip</tt></li>
 * <li><tt>jar:zip:outer.zip!/nested.jar!/somedir</tt></li>
 * <li><tt>jar:zip:outer.zip!/nested.jar!/some%21dir</tt></li>
 * <li><tt>tar:gz:http://anyhost/dir/mytar.tar.gz!/mytar.tar!/path/in/tar/README.txt</tt>
 * </li>
 * <li><tt>tgz:file://anyhost/dir/mytar.tgz!/somepath/somefile</tt>
 * </li>
 * </ul>
 * </div>
 * <div class="section">
 * <h2>
 * <a name="gzip_and_bzip2"></a>gzip and bzip2
 * </h2>
 * <p>Provides read-only access to the contents of gzip and bzip2
 * files.</p>
 * <p>
 * <b>URI Format</b>
 * </p>
 * <p>
 * <tt>
 * gz:// <i>compressed-file-uri</i>
 * </tt>
 * </p>
 * <p>
 * <tt>
 * bz2:// <i>compressed-file-uri</i>
 * </tt>
 * </p>
 * <p>
 * Where
 * <tt>compressed-file-uri</tt>
 * refers to a file of any supported type. There is no need to add a
 * <tt>!</tt>
 * part to the URI if you read the content of the file you always will
 * get the uncompressed version.
 * </p>
 * <p>
 * <b>Examples</b>
 * </p>
 * <ul>
 * <li><tt>gz:/my/gz/file.gz</tt></li>
 * </ul>
 * </div>
 * <div class="section">
 * <h2>
 * <a name="HDFS"></a>HDFS
 * </h2>
 * <p>
 * Provides (read-only) access to files in an Apache Hadoop File System
 * (HDFS). On Windows the <a href="testing.html">integration test</a> is
 * disabled by default, as it requires binaries.
 * </p>
 * <p>
 * <b>URI Format</b>
 * </p>
 * <p>
 * <tt>
 * hdfs:// <i>hostname</i>[: <i>port</i>][ <i>absolute-path</i>]
 * </tt>
 * </p>
 * <p>
 * <b>Examples</b>
 * </p>
 * <ul>
 * <li><tt>hdfs://somehost:8080/downloads/some_dir</tt></li>
 * <li><tt>hdfs://somehost:8080/downloads/some_file.ext</tt></li>
 * </ul>
 * </div>
 * <div class="section">
 * <h2>
 * <a name="HTTP_and_HTTPS"></a>HTTP and HTTPS
 * </h2>
 * <p>Provides access to files on an HTTP server.</p>
 * <p>
 * <b>URI Format</b>
 * </p>
 * <p>
 * <tt>
 * http://[ <i>username</i>[: <i>password</i>]@] <i>hostname</i>[: <i>port</i>][
 * <i>absolute-path</i>]
 * </tt>
 * </p>
 * <p>
 * <tt>
 * https://[ <i>username</i>[: <i>password</i>]@] <i>hostname</i>[: <i>port</i>][
 * <i>absolute-path</i>]
 * </tt>
 * </p>
 * <p>
 * <b>File System Options</b>
 * </p>
 * <ul>
 * <li><b>proxyHost</b> The proxy host to connect through.</li>
 * <li><b>proxyPort</b> The proxy port to use.</li>
 * <li><b>cookies</b> An array of Cookies to add to the request.</li>
 * <li><b>maxConnectionsPerHost</b> The maximum number of
 * connections allowed to a specific host and port. The default is 5.</li>
 * <li><b>maxTotalConnections</b> The maximum number of connections
 * allowed to all hosts. The default is 50.</li>
 * </ul>
 * <p>
 * <b>Examples</b>
 * </p>
 * <ul>
 * <li><tt>http://somehost:8080/downloads/somefile.jar</tt></li>
 * <li><tt>http://myusername@somehost/index.html</tt></li>
 * </ul>
 * </div>
 * <div class="section">
 * <h2>
 * <a name="WebDAV"></a>WebDAV
 * </h2>
 * <p>Provides access to files on a WebDAV server.</p>
 * <p>
 * <b>URI Format</b>
 * </p>
 * <p>
 * <tt>
 * webdav://[ <i>username</i>[: <i>password</i>]@] <i>hostname</i>[: <i>port</i>][
 * <i>absolute-path</i>]
 * </tt>
 * </p>
 * <p>
 * <b>File System Options</b>
 * </p>
 * <ul>
 * <li><b>versioning</b> true if versioning should be enabled</li>
 * <li><b>creatorName</b> the user name to be identified with
 * changes to a file. If not set the user name used to authenticate
 * will be used.</li>
 * </ul>
 * <p>
 * <b>Examples</b>
 * </p>
 * <ul>
 * <li><tt>webdav://somehost:8080/dist</tt></li>
 * </ul>
 * </div>
 * <div class="section">
 * <h2>
 * <a name="FTP"></a>FTP
 * </h2>
 * <p>Provides access to the files on an FTP server.</p>
 * <p>
 * <b>URI Format</b>
 * </p>
 * <p>
 * <tt>
 * ftp://[ <i>username</i>[: <i>password</i>]@] <i>hostname</i>[: <i>port</i>][
 * <i>relative-path</i>]
 * </tt>
 * </p>
 * <p>
 * <b>Examples</b>
 * </p>
 * <ul>
 * <li><tt>ftp://myusername:mypassword@somehost/pub/downloads/somefile.tgz</tt>
 * </li>
 * </ul>
 * <p>By default, the path is relative to the user's home directory.
 * This can be changed with:</p>
 * <p>
 * <tt>FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options,
 * false);</tt>
 * </p>
 * </div>
 * <div class="section">
 * <h2>
 * <a name="FTPS"></a>FTPS
 * </h2>
 * <p>Provides access to the files on an FTP server over SSL.</p>
 * <p>
 * <b>URI Format</b>
 * </p>
 * <p>
 * <tt>
 * ftps://[ <i>username</i>[: <i>password</i>]@] <i>hostname</i>[: <i>port</i>][
 * <i>absolute-path</i>]
 * </tt>
 * </p>
 * <p>
 * <b>Examples</b>
 * </p>
 * <ul>
 * <li><tt>ftps://myusername:mypassword@somehost/pub/downloads/somefile.tgz</tt>
 * </li>
 * </ul>
 * </div>
 * <div class="section">
 * <h2>
 * <a name="SFTP"></a>SFTP
 * </h2>
 * <p>Provides access to the files on an SFTP server (that is, an SSH
 * or SCP server).</p>
 * <p>
 * <b>URI Format</b>
 * </p>
 * <p>
 * <tt>
 * sftp://[ <i>username</i>[: <i>password</i>]@] <i>hostname</i>[: <i>port</i>][
 * <i>relative-path</i>]
 * </tt>
 * </p>
 * <p>
 * <b>Examples</b>
 * </p>
 * <ul>
 * <li><tt>sftp://myusername:mypassword@somehost/pub/downloads/somefile.tgz</tt>
 * </li>
 * </ul>
 * <p>By default, the path is relative to the user's home directory.
 * This can be changed with:</p>
 * <p>
 * <tt>FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options,
 * false);</tt>
 * </p>
 * </div>
 * <div class="section">
 * <h2>
 * <a name="CIFS"></a>CIFS
 * </h2>
 * <p>The CIFS (sandbox) filesystem provides access to a CIFS server,
 * such as a Samba server, or a Windows share.</p>
 * <p>
 * <b>URI Format</b>
 * </p>
 * <p>
 * <tt>
 * smb://[ <i>username</i>[: <i>password</i>]@] <i>hostname</i>[: <i>port</i>][
 * <i>absolute-path</i>]
 * </tt>
 * </p>
 * <p>
 * <b>Examples</b>
 * </p>
 * <ul>
 * <li><tt>smb://somehost/home</tt></li>
 * </ul>
 * </div>
 * <div class="section">
 * <h2>
 * <a name="Temporary_Files"></a>Temporary Files
 * </h2>
 * <p>Provides access to a temporary file system, or scratchpad, that
 * is deleted when Commons VFS shuts down. The temporary file system is
 * backed by a local file system.</p>
 * <p>
 * <b>URI Format</b>
 * </p>
 * <p>
 * <tt>
 * tmp://[ <i>absolute-path</i>]
 * </tt>
 * </p>
 * <p>
 * <b>Examples</b>
 * </p>
 * <ul>
 * <li><tt>tmp://dir/somefile.txt</tt></li>
 * </ul>
 * </div>
 * <div class="section">
 * <h2>
 * <a name="res"></a>res
 * </h2>
 * <p>
 * This is not really a filesystem, it just tries to lookup a resource
 * using javas
 * <tt>ClassLoader.getResource()</tt>
 * and creates a VFS url for further processing.
 * </p>
 * <p>
 * <b>URI Format</b>
 * </p>
 * <p>
 * <tt>
 * res://[ <i>path</i>]
 * </tt>
 * </p>
 * <p>
 * <b>Examples</b>
 * </p>
 * <ul>
 * <li><tt>res:path/in/classpath/image.png</tt><br> might
 * result in <tt>jar:file://my/path/to/images.jar!/path/in/classpath/image.png</tt><br>
 * </li>
 * </ul>
 * </div>
 * <div class="section">
 * <h2>
 * <a name="ram"></a>ram
 * </h2>
 * <p>A filesystem which stores all the data in memory (one byte
 * array for each file content).</p>
 * <p>
 * <b>URI Format</b>
 * </p>
 * <p>
 * <tt>
 * ram://[ <i>path</i>]
 * </tt>
 * </p>
 * <p>
 * <b>File System Options</b>
 * </p>
 * <ul>
 * <li><b>maxsize</b> Maximum filesystem size (total bytes of all
 * file contents).</li>
 * </ul>
 * <p>
 * <b>Examples</b>
 * </p>
 * <ul>
 * <li><tt>ram:///any/path/to/file.txt</tt></li>
 * </ul>
 * </div>
 * <div class="section">
 * <h2>
 * <a name="mime"></a>mime
 * </h2>
 * <p>
 * This (sandbox) filesystem can read mails and its attachements like
 * archives.<br> If a part in the parsed mail has no name, a dummy
 * name will be generated. The dummy name is: _body_part_X where X will
 * be replaced by the part number.
 * </p>
 * <p>
 * <b>URI Format</b>
 * </p>
 * <p>
 * <tt>
 * mime:// <i>mime-file-uri</i>[! <i>absolute-path</i>]
 * </tt>
 * </p>
 * <p>
 * <b>Examples</b>
 * </p>
 * <ul>
 * <li><tt>mime:file:///your/path/mail/anymail.mime!/</tt></li>
 * <li><tt>mime:file:///your/path/mail/anymail.mime!/filename.pdf</tt>
 * </li>
 * <li><tt>mime:file:///your/path/mail/anymail.mime!/_body_part_0</tt>
 * </li>
 * </ul>
 * </div>
 *
 * @author Qt
 * @since Jul 14, 2018
 */
@Slf4j
public class VFSUtils {
	private static FileSystemManager instance;

	static {
		try {
			instance = VFS.getManager();
		} catch (FileSystemException e) {
			log.error("init vfs fileSystemManager fail.", e);
			throw new IllegalStateException(e);
		}
	}

	public static FileSystemManager getFileSystemManager() {
		return instance;
	}

	public static FileSystemOptions buildSftpOpts(File privateKey) throws FileSystemException {
		return buildSftpOpts(privateKey, null);
	}

	public static FileSystemOptions buildSftpOpts(File privateKey, byte[] passPhrase) throws FileSystemException {
		FileSystemOptions opts = new FileSystemOptions();
		SftpFileSystemConfigBuilder builder = SftpFileSystemConfigBuilder.getInstance();
		builder.setUserDirIsRoot(opts, false);
		builder.setIdentityInfo(opts, new IdentityInfo(privateKey, passPhrase));
		return opts;
	}

	public static <R> R readFile(String filePath, FileSystemOptions opts, ReadFunc<InputStream, R> function)
		throws IOException {
		if (StringUtils.isEmpty(filePath)) {
			throw new IOException("File '" + filePath + "' is empty.");
		}
		FileObject fileObj = null;
		InputStream in = null;
		try {
			if (opts != null) {
				fileObj = getFileSystemManager().resolveFile(filePath, opts);
			} else {
				fileObj = getFileSystemManager().resolveFile(filePath);
			}

			if (fileObj.exists()) {
				if (FileType.FOLDER.equals(fileObj.getType())) {
					throw new IOException("File '" + filePath + "' exists but is a directory");
				} else {
					in = fileObj.getContent().getInputStream();
					return function.apply(in);
				}
			} else {
				throw new FileNotFoundException("File '" + filePath + "' does not exist");
			}
		} catch (FileSystemException e) {
			throw new IOException("File '" + filePath + "' resolveFile fail.");
		} finally {
			org.apache.commons.io.IOUtils.closeQuietly(in);
			org.apache.commons.io.IOUtils.closeQuietly(fileObj);
		}

	}

	/**
	 * 读取文件, 将其中内容以byte进行输出
	 */
	public static byte[] readFileToByteArray(String filePath)
		throws IOException {
		return readFile(filePath, null, new ReadFunc<InputStream, byte[]>() {
			@Override
			public byte[] apply(InputStream in) throws IOException {
				return org.apache.commons.io.IOUtils.toByteArray(in);
			}
		});
	}

	/**
	 * 读取文件内容
	 */
	public static String readFileToString(String filePath, final String encoding)
		throws IOException {
		return readFile(filePath, null, new ReadFunc<InputStream, String>() {
			@Override
			public String apply(InputStream in) throws IOException {
				return org.apache.commons.io.IOUtils.toString(in, encoding);
			}
		});
	}

	/**
	 * 读取文件内容
	 */
	public static String readFileToString(String filePath)
		throws IOException {
		return readFile(filePath, null, new ReadFunc<InputStream, String>() {
			@Override
			public String apply(InputStream in) throws IOException {
				return org.apache.commons.io.IOUtils.toString(in);
			}
		});
	}

	public static List<String> readLines(String filePath)
		throws IOException {
		return readFile(filePath, null, new ReadFunc<InputStream, List<String>>() {
			@Override
			public List<String> apply(InputStream in) throws IOException {
				return org.apache.commons.io.IOUtils.readLines(in);
			}
		});
	}

	public static List<String> readLines(String filePath, final String encoding)
		throws IOException {
		return readFile(filePath, null, new ReadFunc<InputStream, List<String>>() {
			@Override
			public List<String> apply(InputStream in) throws IOException {
				return org.apache.commons.io.IOUtils.readLines(in, encoding);
			}
		});
	}

	public static void writeToFile(String filePath, FileSystemOptions opts, WriteFunc<OutputStream> writeFunc) throws IOException {
		if (StringUtils.isEmpty(filePath)) {
			throw new IOException("File '" + filePath + "' is empty.");
		}
		FileObject fileObj = null;
		OutputStream out = null;
		try {
			if (opts != null) {
				fileObj = getFileSystemManager().resolveFile(filePath, opts);
			} else {
				fileObj = getFileSystemManager().resolveFile(filePath);
			}

			if (!fileObj.exists()) {
				fileObj.createFile();
			} else {
				if (FileType.FOLDER.equals(fileObj.getType())) {
					throw new IOException("Fail to write. File '" + filePath + "' exists but is a directory");
				}
			}
			if (!fileObj.isWriteable()) {
				throw new IOException("Fail to write. File '" + filePath + "' exists but cannot write.");
			}
			out = fileObj.getContent().getOutputStream();
			writeFunc.apply(out);
			out.flush();
		} catch (FileSystemException e) {
			throw new IOException("File '" + filePath + "' resolveFile fail.");
		} finally {
			org.apache.commons.io.IOUtils.closeQuietly(out);
			org.apache.commons.io.IOUtils.closeQuietly(fileObj);
		}
	}

	/**
	 * 将内容写入文件中
	 */
	public static void writeToFile(String filePath, final CharSequence data, final String encoding) throws IOException {
		writeToFile(filePath, null, new WriteFunc<OutputStream>() {
			@Override
			public void apply(OutputStream out) throws IOException {
				org.apache.commons.io.IOUtils.write(data, out, encoding);
			}

		});
	}

	/**
	 * 将内容写入文件中
	 */
	public static void writeToFile(String filePath, final CharSequence data) throws IOException {
		writeToFile(filePath, null, new WriteFunc<OutputStream>() {
			@Override
			public void apply(OutputStream out) throws IOException {
				org.apache.commons.io.IOUtils.write(data, out);
			}

		});
	}

	/**
	 * 将内容写入文件中
	 */
	public static void writeToFile(String filePath, final byte[] data) throws IOException {
		writeToFile(filePath, null, new WriteFunc<OutputStream>() {
			@Override
			public void apply(OutputStream out) throws IOException {
				org.apache.commons.io.IOUtils.write(data, out);
			}

		});
	}

	/**
	 * 将内容写入文件中
	 */
	public static void writeToFile(String filePath, final char[] data, final String encoding) throws IOException {
		writeToFile(filePath, null, new WriteFunc<OutputStream>() {
			@Override
			public void apply(OutputStream out) throws IOException {
				org.apache.commons.io.IOUtils.write(data, out, encoding);
			}

		});
	}

	/**
	 * 将内容写入文件中
	 */
	public static void writeToFile(String filePath, final char[] data) throws IOException {
		writeToFile(filePath, null, new WriteFunc<OutputStream>() {
			@Override
			public void apply(OutputStream out) throws IOException {
				IOUtils.write(data, out);
			}

		});
	}

	@FunctionalInterface
	public static interface ReadFunc<T, R> {
		R apply(T t) throws IOException;
	}

	@FunctionalInterface
	public static interface WriteFunc<T> {
		void apply(T t) throws IOException;
	}
}
