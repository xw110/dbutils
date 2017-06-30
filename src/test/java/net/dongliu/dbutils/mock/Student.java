package net.dongliu.dbutils.mock;

import net.dongliu.dbutils.mapping.Column;

import java.time.LocalDate;

public class Student {
    private long id;
    public String name;
    private int age = 0;
    @Column("isMale")
    private boolean male;
    private LocalDate birth;

    public Student() {
    }

    public Student(long id, String name, int age, boolean male, LocalDate birth) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.male = male;
        this.birth = birth;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    @Column("birthDay")
    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", male=" + male +
                ", birth=" + birth +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        if (id != student.id) return false;
        if (age != student.age) return false;
        if (male != student.male) return false;
        if (name != null ? !name.equals(student.name) : student.name != null) return false;
        return birth != null ? birth.equals(student.birth) : student.birth == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + age;
        result = 31 * result + (male ? 1 : 0);
        result = 31 * result + (birth != null ? birth.hashCode() : 0);
        return result;
    }
}
