/*
 * Copyright 2017 - 2019 saftsau
 *
 * This file is part of xREL4J.
 *
 * xREL4J is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * xREL4J is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with xREL4J. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.github.saftsau.xrel4j;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Class used for any other classes that use pagination in their results.
 */
public class Pagination implements Serializable {

  @JsonProperty("current_page")
  private int currentPage;
  @JsonProperty("per_page")
  private int perPage;
  @JsonProperty("total_pages")
  private long totalPages;

  /**
   * Gets the current page this pagination.
   * 
   * @return The current page
   */
  public int getCurrentPage() {
    return currentPage;
  }

  /**
   * Sets the current page this pagination.
   * 
   * @param currentPage The current page to set
   */
  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }

  /**
   * Gets the items per page of this pagination.
   * 
   * @return The items per page
   */
  public int getPerPage() {
    return perPage;
  }

  /**
   * Sets the items per page of this pagination.
   * 
   * @param perPage The items per page to set
   */
  public void setPerPage(int perPage) {
    this.perPage = perPage;
  }

  /**
   * Gets the total pages of this pagination.
   * 
   * @return The total pages
   */
  public long getTotalPages() {
    return totalPages;
  }

  /**
   * Sets the total pages of this pagination.
   * 
   * @param totalPages The total pages to set
   */
  public void setTotalPages(long totalPages) {
    this.totalPages = totalPages;
  }

  @Override
  public String toString() {
    return "Pagination [getCurrentPage()=" + getCurrentPage() + ", getPerPage()=" + getPerPage()
        + ", getTotalPages()=" + getTotalPages() + "]";
  }

}
