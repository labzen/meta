package cn.labzen.meta.exception;

import org.slf4j.helpers.MessageFormatter;

public class LabzenRuntimeException extends RuntimeException {

  public LabzenRuntimeException(String message) {
    super(message);
  }

  public LabzenRuntimeException(String message, Object... args) {
    super(MessageFormatter.basicArrayFormat(message, args));
  }

  public LabzenRuntimeException(Throwable cause) {
    super(cause);
  }

  public LabzenRuntimeException(Throwable cause, String message) {
    super(message, cause);
  }

  public LabzenRuntimeException(Throwable cause, String message, Object... args) {
    super(MessageFormatter.basicArrayFormat(message, args), cause);
  }
}
