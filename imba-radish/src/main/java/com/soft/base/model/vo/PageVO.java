package com.soft.base.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

@Schema(description = "分页")
@Data
@Alias(value = "PageVo")
public class PageVO<T> {

    @Schema(description = "响应结果")
    private List<T> records;

    @Schema(description = "总数", example = "10")
    private Long total;

    public PageVO() {}

    public PageVO(List<T> records, Long total) {
        this.records = records;
        this.total = total;
    }

    public static <T> PageVOBuilder<T> builder() {
        return new PageVOBuilder<>();
    }

    public static class PageVOBuilder<T> {
        private List<T> records;
        private Long total;

        PageVOBuilder() {}

        public PageVOBuilder<T> records(List<T> records) {
            this.records = records;
            return this;
        }

        public PageVOBuilder<T> total(Long total) {
            this.total = total;
            return this;
        }

        public PageVO<T> build() {
            return new PageVO<>(records, total);
        }
    }

}
