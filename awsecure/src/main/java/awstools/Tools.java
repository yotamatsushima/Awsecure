package awstools;

import com.amazonaws.services.ec2.model.Tag;
import java.util.List;

public class Tools
{
  public static String getTagValue(List<Tag> tags, String key)
  {
    String str = "";
    for (Tag tag : tags) {
      if (tag.getKey().toUpperCase().equals(key.toUpperCase()))
      {
        str = tag.getValue();
        break;
      }
    }
    return str;
  }
}
