/*  
 * Copyright 2008 CoreMedia AG, Hamburg
 *
 * Licensed under the Apache License, Version 2.0 (the License); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an AS IS BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */

package com.coremedia.iso.boxes.odf;

import com.coremedia.iso.BoxFactory;
import com.coremedia.iso.IsoInputStream;
import com.coremedia.iso.IsoOutputStream;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.FullBoxContainer;
import com.coremedia.iso.boxes.UserDataBox;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * The Discrete Media headers box includes fields specific to the DCF format and the
 * {@link com.coremedia.iso.boxes.odf.OmaDrmCommonHeadersBox}, followed by an optional
 * {@link com.coremedia.iso.boxes.UserDataBox}.
 */
public class OmaDrmDiscreteHeadersBox extends FullBoxContainer {
  public static final String TYPE = "odhe";

  private Box[] boxes;
  private String contentType;

  public OmaDrmDiscreteHeadersBox() {
    super(TYPE);
  }

  public Box[] getBoxes() {
    return boxes;
  }

  @SuppressWarnings("unchecked")
  public <T extends Box> T[] getBoxes(Class<T> clazz) {
    ArrayList<T> boxesToBeReturned = new ArrayList<T>();
    for (Box boxe : boxes) {
      if (clazz.isInstance(boxe)) {
        boxesToBeReturned.add(clazz.cast(boxe));
      }
    }
    return boxesToBeReturned.toArray((T[]) Array.newInstance(clazz, boxesToBeReturned.size()));
  }

  public String getContentType() {
    return contentType;
  }

  public String getDisplayName() {
    return "OMA DRM Discrete Headers Box";
  }

  protected long getContentSize() {
    long size = 0;
    for (Box boxe : boxes) {
      size += boxe.getSize();
    }
    return size + 1 + utf8StringLengthInBytes(contentType);
  }

  public void parse(IsoInputStream in, long size, BoxFactory boxFactory, Box lastMovieFragmentBox) throws IOException {
    parseHeader(in, size);
    int contentTypeLength = in.readUInt8();
    contentType = new String(in.read(contentTypeLength), "UTF-8");
    List<Box> boxList = new LinkedList<Box>();
    long remainingContentSize = size - 4 - 1 - contentTypeLength;
    while (remainingContentSize > 0) {
      Box box = boxFactory.parseBox(in, this, lastMovieFragmentBox);
      remainingContentSize -= box.getSize();
      boxList.add(box);
    }
    this.boxes = boxList.toArray(new Box[boxList.size()]);
  }

  protected void getContent(IsoOutputStream isos) throws IOException {
    long sp = isos.getStreamPosition();
    isos.writeUInt8(utf8StringLengthInBytes(contentType));
    isos.writeStringNoTerm(contentType);
    for (Box boxe : boxes) {
      boxe.getBox(isos);
    }
    assert isos.getStreamPosition() - sp == getContentSize();
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("OmaDrmDiscreteHeadersBox[");
    buffer.append("contentType=").append(getContentType());
    Box[] boxes2 = getBoxes();
    for (Box aBoxes2 : boxes2) {
      buffer.append(";");
      buffer.append(aBoxes2.toString());
    }
    buffer.append("]");
    return buffer.toString();
  }

  public OmaDrmCommonHeadersBox getOmaDrmCommonHeadersBox() {
    for (Box box : boxes) {
      if (box instanceof OmaDrmCommonHeadersBox) {
        return (OmaDrmCommonHeadersBox) box;
      }
    }
    return null;
  }

  public UserDataBox getUserDataBox() {
    for (Box box : boxes) {
      if (box instanceof UserDataBox) {
        return (UserDataBox) box;
      }
    }
    return null;
  }
}
