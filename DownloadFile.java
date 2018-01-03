//*********
// Monica Arbeit - Vimeo Test
// 1/2/18
//
//*********

import java.net.URL;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.io.IOException;

import java.security.MessageDigest;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import java.util.List;
import java.util.Map;

public class DownloadFile {


	private static String video_link = "https://storage.googleapis.com/vimeo-test/work-at-vimeo-2.mp4";
	private static boolean get_request = false;
	private static boolean downloaded = false;		// if downloaded, will calculate digest code
	private static boolean accept_range = false;		

	public String getMessage() {
		return ("File failed to be downloaded");
	}

	public static File vimeo_dload (String source) {

		// Create path for download file to user's download folder
		String fileName = "vimeo_test.mp4";
		String home = System.getProperty("user.home");
		File file = new File(home + "/Downloads/" + fileName);

		try {

			URL link = new URL (source);
			HttpURLConnection con = getConnection(link);
			FileOutputStream fos = new FileOutputStream(file);
		
			// Check if file server supports GET requests
			String method = con.getRequestMethod();
			if (method == "GET")
				get_request = true;

			int status = con.getResponseCode(); 
			InputStream is = con.getInputStream();
			int file_size = con.getContentLength();

			// Check that server gave appropriate file size
			if (file_size < 0) 
				System.out.println("Didn't receive correct file size");
			else {
				System.out.println("(File size is: " + file_size + ")");
			}

			// Check to see file server supports byte range requests (to download in chunks)
			Map<String, List<String>> map = con.getHeaderFields();
			List<String> acceptRange = map.get("Accept-Ranges");
			if (acceptRange == null)
				System.out.println("Accept Ranges is null");
			else {
				accept_range = true;
			}

			// If status is 206 Partial Content, then download file in chunks
			int count = 0;
			if (status == 206 && get_request && accept_range) {
				
				int chunks = file_size / 100;

				// Download each chunk
				byte[] temp = new byte[chunks + 1];
				int bytes = 0;
			
				while(( bytes = is.read(temp)) != -1 ) {
					count++;

					// Printing benchmarks for various chunk sizes
					if (count == 5000) {
						
						System.out.print(".");
						count = 0;
					}

					fos.write(temp, 0, bytes);
				}	

				downloaded = true;
			}
			// If status is 200 OK response, download entire file
			else if (status == 200) {

				byte[] buffer = new byte[4069];
				int len1 = 0;	
				while ((len1 = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len1);
				}

				downloaded = true;

			}

			System.out.print(" 100% DONWLOADED\n");

			fos.close();
			is.close();
			con.disconnect();

		} catch (IOException e) {
			e.getMessage();
		}

		return file;

	}

	private static HttpURLConnection getConnection (URL link) {

		try {
			int retry = 0;
			int RETRIES = 5;
			do {

				HttpURLConnection con = (HttpURLConnection) link.openConnection();

				// Testing different types of HTTP connection responses
				switch (con.getResponseCode()) {
					case HttpURLConnection.HTTP_OK:
						System.out.print("Beginning download: ");
						return con;
					case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
						System.out.println("GATEWAY TIMEOUT\n");
						break;
					case HttpURLConnection.HTTP_UNAVAILABLE:
						System.out.println("UNAVAILABLE");
						break;
					default:
						System.out.println("UNKNOWN RESPONSE CODE");
						break;
				}
				con.disconnect();
				retry++;
				System.out.println("Failed retry " + retry);

			} while (retry < RETRIES);

			System.out.println("Aborting download \n");

		} catch (IOException e){
			System.out.println("HTTP Connection failed to be made");
		}

		return null;

	}

	public static void main(String[] args) throws Exception{

		File file = vimeo_dload(video_link);

		try  {

			if (downloaded) {

				// Check downloaded file's integrity using MD5 digest code
				MessageDigest md = MessageDigest.getInstance("MD5");
				String str_file = file.toString();
				FileInputStream incoming = new FileInputStream(str_file);

				byte[] dataBytes = new byte[1024];
				int nread = 0;

				while ((nread = incoming.read(dataBytes)) != -1 ) {
					md.update(dataBytes, 0, nread);
				};

				byte[] mdbytes = md.digest();

				StringBuffer sb = new StringBuffer("");
				for (int i = 0; i < mdbytes.length; i++) {
					sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
				}

				String local_file = "4146168a0ea3e634f6707d9a189cb5e4";

				if (local_file.equals(sb.toString()))
					System.out.println("Downloaded file's integrity is maintained");
				else
					System.out.println("Downloaded file's integrity is NOT maintained");

				incoming.close();
			}
			
		}
		catch (IOException e) {
			System.out.println("The file's MD5 message digest code for file failed");
		}

	}


}


