package inorg.gsdl.plndpt;


import androidx.annotation.NonNull;

public class MyUserData {


    public String userid;
    public String password;
    public String status;
    public String mobile;
    public String deptcode;
    public String timestamp;
    public String name;
    private String dptname;

    public String getUserId() {
        return userid;
    }

    public void setUserId(String userId) {
        this.userid = userId;
    }

    public String getDptname() {
        return dptname;
    }

    public void setDptname(String dptname) {
        this.dptname = dptname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDeptcode() {
        return deptcode;
    }

    public void setDeptcode(String deptcode) {
        this.deptcode = deptcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return "MyUserData{" +
                "userid='" + userid + '\'' +
                ", dptname='" + dptname + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", mobile='" + mobile + '\'' +
                ", deptcode='" + deptcode + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
