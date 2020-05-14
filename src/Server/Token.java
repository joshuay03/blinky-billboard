package Server;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Random;

class Token {
    public Timestamp expiryDate;
    public byte[] token;
    public String username;

    Token(byte[] token, String username){
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        this.token = token;
        this.expiryDate = Timestamp.valueOf(tomorrow);
        this.username = username;
    }
}
