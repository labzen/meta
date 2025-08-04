package cn.labzen.meta.exception;

import org.slf4j.helpers.MessageFormatter;

public class LabzenException extends Exception {

  public LabzenException(String message) {
    super(message);
  }

  public LabzenException(String message, String... args) {
    super(MessageFormatter.basicArrayFormat(message, args));
  }

  public LabzenException(Throwable cause) {
    super(cause);
  }

  public LabzenException(Throwable cause, String message) {
    super(message, cause);
  }

  public LabzenException(Throwable cause, String message, String... args) {
    super(MessageFormatter.basicArrayFormat(message, args), cause);
  }
}
