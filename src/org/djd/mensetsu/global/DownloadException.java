package org.djd.mensetsu.global;

public class DownloadException extends Exception {

    enum ERROR_CODE {
        HTTP_PROTOCOL_ERROR, FAILED_CREATE_HTTP_CONNECTION, 
        STREAM_COULD_NOT_BE_CREATED, ENTITY_IS_NOT_REPEATABLE,
        READ_FILE_ERROR
    };
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public DownloadException(ERROR_CODE eCode){
        super(eCode.toString());
    }
    
   
}
