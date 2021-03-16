package inorg.gsdl.plndpt;

public class User {
    private String dptname;

    private String project;

    private String projectid;

    private String name;


    private String userid;


    private String mobile;


    private String lat;

    private String log;


    private String images;


    private String progress;

    private String timestamp;


    private String reportid;


    private String description;

    private String status;

    public User() {

    }

    public User(String project, String timestamp, String reportid) {
        this.project = project;
        this.timestamp = timestamp;
        this.reportid = reportid;
    }

    public User(String dptname, String project, String projectid, String name, String userid, String mobile, String lat, String log, String images, String progress, String timestamp, String reportid, String description, String status) {
        this.dptname = dptname;
        this.project = project;
        this.projectid = projectid;
        this.name = name;
        this.userid = userid;
        this.mobile = mobile;
        this.lat = lat;
        this.log = log;
        this.images = images;
        this.progress = progress;
        this.timestamp = timestamp;
        this.reportid = reportid;
        this.description = description;
        this.status = status;
    }

    public String getDptname() {
        return dptname;
    }

    public void setDptname(String dptname) {
        this.dptname = dptname;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getReportid() {
        return reportid;
    }

    public void setReportid(String reportid) {
        this.reportid = reportid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getProjectid() {
        return projectid;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }
}


