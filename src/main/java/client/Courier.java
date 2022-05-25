package client;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * Описание данных курьера.
 */
public class Courier {
    private String login;
    private String password;
    private String firstName;
}