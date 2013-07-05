import java.util.Date;
import java.text.SimpleDateFormat;


/*
 *  Class modeling the default options common to many Parsely API endpoints
 */
public class RequestOptions{
    public static class Builder{
        public RequestOptions build(){
            return new RequestOptions(this);
        }

        public Builder withDays(int days){
            this.days = days;
            return this;
        }

        public Builder withLimit(int limit){
            this.limit = limit;
            return this;
        }

        public Builder withPage(int page){
            this.page = page;
            return this;
        }

        public Builder withSort(String sort){
            this.sort = sort;
            return this;
        }

        public Builder withDateRange(Date start, Date end){
            this.start = start;
            this.end = end;
            return this;
        }

        public Builder withPubDateRange(Date start, Date end){
            this.pub_start = start;
            this.pub_end = end;
            return this;
        }

        private int days = 14, limit = 10, page = 1;
        private Date start, end, pub_start, pub_end;
        private String sort = "_hits";
    }

    public static Builder builder(){
        return new Builder();
    }

    private RequestOptions(Builder builder){
        this.days = builder.days;
        this.limit = builder.limit;
        this.page = builder.page;
        this.start = builder.start;
        this.end = builder.end;
        this.pub_start = builder.pub_start;
        this.pub_end = builder.pub_end;
        this.sort = builder.sort;
    }

    private int days, limit, page;
    private Date start, end, pub_start, pub_end;
    private String sort;

    public String getAsQueryString(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String sf = "days=%d&limit=%d&page=%d&sort=%s&period_start=%s&period_end=%s&pub_date_start=%s&pub_date_end=%s";

        String startf, endf, pub_startf, pub_endf;
        startf = start == null ? "" : df.format(start);
        endf = end == null ? "" : df.format(end);
        pub_startf = pub_start == null ? "" : df.format(pub_start);
        pub_endf = pub_end == null ? "" : df.format(pub_end);

        return String.format(sf, days, limit, page, sort, startf, endf,
                             pub_startf, pub_startf
                            );
    }
}
