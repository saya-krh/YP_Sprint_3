import client.CourierClient;
import client.Courier;
import client.CourierGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class RegisterCourierTest {

    CourierClient courierClient;
    Courier courier;
    int courierId;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = CourierGenerator.getRandom();
    }

    @After
    public void tearDown(){
        if(courierId != 0){
            courierClient.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Курьер может быть создан")
    public void courierCanBeCreated(){
        ValidatableResponse createResponse = courierClient.createCourier(courier);
        int statusCode = createResponse.extract().statusCode();
        boolean result = createResponse.extract().path("ok");

        assertThat("Can't create courier", statusCode, equalTo(SC_CREATED));
        assertThat(String.valueOf(result), true);
    }

    @Test
    @DisplayName("Нельзя создать курьера без Логина")
    public void courierCantBeCreatedWithoutLogin(){
        ValidatableResponse createResponse = courierClient.createCourier(new Courier("", courier.getPassword(), courier.getFirstName()));
        int statusCode = createResponse.extract().statusCode();
        String result = createResponse.extract().path("message");

        assertThat("Can't create courier", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat(result, equalTo("Привет"));
    }

    @Test
    @DisplayName("Нельзя создать курьера без Пароля")
    public void courierCantBeCreatedWithoutPassword(){
        ValidatableResponse createResponse = courierClient.createCourier(new Courier(courier.getLogin(), "", courier.getFirstName()));
        int statusCode = createResponse.extract().statusCode();
        String result = createResponse.extract().path("message");

        assertThat("Courier created", statusCode, equalTo(SC_BAD_REQUEST));
        assertThat(result, equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void courierCantBeCreatedWithExistingLogin(){
        courierClient.createCourier(new Courier("Login", courier.getPassword(), courier.getFirstName()));
        ValidatableResponse createResponse = courierClient.createCourier(new Courier("Login", courier.getPassword(), courier.getFirstName()));
        int statusCode = createResponse.extract().statusCode();
        String result = createResponse.extract().path("message");

        assertThat("Courier created", statusCode, equalTo(SC_CONFLICT));
        assertThat(result, equalTo("Этот логин уже используется"));
    }
}