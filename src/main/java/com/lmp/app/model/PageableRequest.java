package com.lmp.app.model;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageableRequest {

  @Min(0)
  private int page;
  @Min(0)
  @Max(50)
  private int rows;

  public PageableRequest() {
    this.page = 0;
    this.rows = 10;
  }
  public PageableRequest(int page, int rows) {
    this.page = page;
    this.rows = rows;
  }

  public long fetchedCount() {
    return (this.page) * this.rows;
  }

  public Pageable pageRequesst() {
    return new PageRequest(getPage(), getRows());
  }

  public int getPage() {
    return page;
  }
  public void setPage(int page) {
    this.page = page;
  }
  public int getRows() {
    return rows;
  }
  public void setRows(int rows) {
    this.rows = rows;
  }
}
