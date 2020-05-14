package Server;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Random;

class Token {
    public Timestamp expiryDate;
    public byte[] token;

    Token(byte[] token){
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        this.token = token;
        this.expiryDate = Timestamp.valueOf(tomorrow);
    }
}
