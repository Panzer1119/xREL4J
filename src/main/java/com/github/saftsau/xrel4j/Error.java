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
 * Class represents an Error. Used for all information the xREL API offers when returning errors.
 * 
 * @see <a href="https://www.xrel.to/wiki/6435/api-errors.html">API: Error Handling</a>
 */
public class Error implements Serializable {
  
  @JsonProperty("error")
  public String errorName;
  @JsonProperty("error_description")
  public String errorDescription;
  @JsonProperty("error_type")
  public String errorType;

  /**
   * Gets the error.
   * 
   * @return The error
   */
  public String getError() {
    return errorName;
  }

  /**
   * Gets the description of the error.
   * 
   * @return The description of the error
   */
  public String getErrorDescription() {
    return errorDescription;
  }

  /**
   * Gets the type of the error.
   * 
   * @return The type of the error
   */
  public String getErrorType() {
    return errorType;
  }

  /**
   * Sets the error.
   * 
   * @param error The error to set
   */
  public void setError(String error) {
    this.errorName = error;
  }

  /**
   * Sets the description of the error.
   * 
   * @param errorDescription The description of the error to set
   */
  public void setErrorDescription(String errorDescription) {
    this.errorDescription = errorDescription;
  }

  /**
   * Sets the type of the error.
   * 
   * @param errorType The type of the error to set
   */
  public void setErrorType(String errorType) {
    this.errorType = errorType;
  }

  @Override
  public String toString() {
    return "Error [getError()=" + getError() + ", getErrorDescription()=" + getErrorDescription()
        + ", getErrorType()=" + getErrorType() + "]";
  }

}
