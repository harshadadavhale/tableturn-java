package com.tableturn.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tableturn")
public class TableTurnSettings {

    private List<TableDefinition> tables = new ArrayList<>();

    public List<TableDefinition> getTables() {
        return tables;
    }

    public void setTables(List<TableDefinition> tables) {
        this.tables = tables;
    }

    public static class TableDefinition {

        private String branch;
        private Integer number;
        private Integer size;
        private String section;
        private String state;

        public String getBranch() {
            return branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        public String getSection() {
            return section;
        }

        public void setSection(String section) {
            this.section = section;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }
}