README for DownloadFile.java || Vimeo Coding Challenge || By Monica Arbeit

A program for downloading a given file.

The program consists of one java file, DownloadFile.java. To run in terminal: $javac DownloadFile.java to make execulatable, then $java DownloadFile to run program.

Checking File Server Byte Range GET request:
The program downloads video from a hard-coded link. I assumed this was the best approach since we are only given one link. I created a path for the FileOutputStream to download to the user's download folder. If the file server does support byte range GET requests it will download the video in chunks. The program prints a "." at every increment of 5000 iterations of reading/writing when downloading in chunks. I also control the chunks being downloaded by the filesize/100. I saw this was an appropriate way to split the chunks. If the file server doesn't support this it will download the whole video.

Handles Errors & Retries:
The program handles errors and retries including: gateway timeout, unavailable link, and an unknown response code. Based on 3 max retries, if the HTTP connection doesn't work correctly I will abort the program.
I followed this link for information of the file server download errors: https://codereview.stackexchange.com/questions/45819/httpurlconnection-response-code-handling. This helped me see the different types of HTTP connections to check for.

Checks Downloaded File Integrity:
The program checks the downloaded file's integrity based on a local version of the video on my computer. The MD5 code is a String in my program and if the file downloads correctly, I will compare the two and print whether they match.
I followed this question to understand how to implement MD5's code: https://stackoverflow.com/questions/304268/getting-a-files-md5-checksum-in-java. This was helpful in learning about MessageDigest.

I choose Java for this program over the other language I'm familar with (C/C++), mostly so I didn't have to work with memory allocation. I didn't use any external libraries. I saw many different github codes for downloading a youtube and/or vimeo video that had to parse through the link and handle other factors. Since this link is only a video page (not a video platform), I decided to take a simpler approach based off of the vimeo-test's specifications.
