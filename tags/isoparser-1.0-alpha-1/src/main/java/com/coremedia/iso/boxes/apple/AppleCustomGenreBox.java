package com.coremedia.iso.boxes.apple;

import com.coremedia.iso.Utf8;

/**
 *
 */
public final class AppleCustomGenreBox extends AbstractAppleMetaDataBox {
  public static final String TYPE = "\u00a9gen";


  public AppleCustomGenreBox() {
    super(TYPE);
  }

  public String getDisplayName() {
    return "iTunes Custom Genre";
  }


  public void setGenre(String genre) {
    appleDataBox = new AppleDataBox();
    appleDataBox.setVersion(0);
    appleDataBox.setFlags(1);
    appleDataBox.setFourBytes(new byte[4]);
    appleDataBox.setContent(Utf8.convert(genre));
  }

  public String getGenre() {
    return Utf8.convert(appleDataBox.getContent());
  }
}