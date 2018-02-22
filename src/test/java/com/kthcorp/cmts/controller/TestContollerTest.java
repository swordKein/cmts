package com.kthcorp.cmts.controller;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

/*
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
*/
@RunWith(SpringRunner.class)
class TestControllerTest {
/*
    @Autowired
    private WelcomeController restTemplate;

    @Test
    public void testHomeJsp() throws Exception {
        ResponseEntity<String> entity = this.restTemplate.getForEntity("/", String.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = entity.getBody();
        assertThat(body).contains("<html>").contains("<h1>Home</h1>");
    }

    @Test
    public void testStaticPage() throws Exception {
        ResponseEntity<String> entity = this.restTemplate.getForEntity("/index.html",
                String.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        String body = entity.getBody();
        assertThat(body).contains("<html>").contains("<h1>Hello</h1>");
    }
*/
}
