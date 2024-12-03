package school.sptech;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse {

    private Candidate[] candidates;

    public Candidate[] getCandidates() {
        return candidates;
    }

    public void setCandidates(Candidate[] candidates) {
        this.candidates = candidates;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate {
        private Content content;

        public Content getContent() {
            return content;
        }

        public void setContent(Content content) {
            this.content = content;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        private Part[] parts;

        public Part[] getParts() {
            return parts;
        }

        public void setParts(Part[] parts) {
            this.parts = parts;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
