package ca.mcgill.ecse428.foodme.model;

public class Response {

        private boolean response;

        private String message;
        public Response() {
        }

        public Response(boolean response,String message){
            this.response = response;
            this.message = message;
        }

        public boolean getResponse() {
            return response;
        }

        public void setResponse(boolean response) {
            this.response = response;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

}
