package ca.mcgill.ecse428.foodme.model;

public class Response {

        private boolean response;

        public boolean getResponse() {
            return response;
        }

        public void setResponse(boolean response) {
            this.response = response;
        }

        private String error;

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

}
