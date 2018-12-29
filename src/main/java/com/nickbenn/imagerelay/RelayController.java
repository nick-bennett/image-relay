package com.nickbenn.imagerelay;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("images")
public class RelayController {

  @Value("${base-source-url}")
  private String baseSourceUrl;
  @Value("${transfer-buffer-size}")
  private int bufferSize;

  private OkHttpClient client;

  @Autowired
  public RelayController(OkHttpClient client) {
    this.client = client;
  }

  @GetMapping(
      value = "{imageId}",
      produces = {
          MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE,
          "video/webm", "video/mp4", MediaType.APPLICATION_OCTET_STREAM_VALUE
      }
  )
  public StreamingResponseBody get(@PathVariable("imageId") String imageId,
      HttpServletResponse relayResponse) throws IOException {
    Request request = new Request.Builder()
        .url(baseSourceUrl + imageId)
        .build();
    Response response = client.newCall(request).execute();
    if (response.isSuccessful()) {
      return (out) -> {
        ResponseBody body = response.body();
        InputStream input = body.byteStream();
        byte[] buffer = new byte[bufferSize];
        relayResponse.setContentType(body.contentType().toString());
        relayResponse.setContentLengthLong(body.contentLength());
        for (int bytesRead = input.read(buffer);
            bytesRead >= 0;
            bytesRead = input.read(buffer)) {
          out.write(buffer, 0, bytesRead);
        }
        out.flush();
      };
    }
    return null;
  }

}
