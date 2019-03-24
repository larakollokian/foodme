package ca.mcgill.ecse428.foodme.model;

public class Response {

        //Attributes
        private boolean response;
        private String message;

        //Constructor
        public Response(boolean response,String message){
            this.response = response;
            this.message = message;
        }

        //Getters
        public boolean getResponse() { return response; }
        public String getMessage() { return message; }

        //Setters
        public void setResponse(boolean response) { this.response = response; }
        public void setMessage(String message) { this.message = message; }

}
