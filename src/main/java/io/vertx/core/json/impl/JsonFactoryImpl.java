/*
 * Copyright (c) 2011-2017 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package io.vertx.core.json.impl;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.EncodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.JsonFactory;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public class JsonFactoryImpl implements JsonFactory {

  @Override
  public <T> T fromValue(Object json, Class<T> clazz) {
    T value = Json.mapper.convertValue(json, clazz);
    if (clazz == Object.class) {
      value = clazz.cast(adapt(value));
    }
    return value;
  }

  @Override
  public <T> T fromString(String str, Class<T> clazz) throws DecodeException {
    T value = Json.decodeValue(str, clazz);
    if (clazz == Object.class) {
      value = clazz.cast(adapt(value));
    }
    return value;
  }

  @Override
  public <T> T fromBuffer(Buffer json, Class<T> clazz) throws DecodeException {
    T value = Json.decodeValue(json, clazz);
    if (clazz == Object.class) {
      value = clazz.cast(adapt(value));
    }
    return value;
  }

  @Override
  public String toString(Object object, boolean pretty) throws EncodeException {
    if (pretty) {
      return Json.encodePrettily(object);
    } else {
      return Json.encode(object);
    }
  }

  private Object adapt(Object o) {
    try {
      if (o instanceof List) {
        List list = (List) o;
        return new JsonArray(list);
      } else if (o instanceof Map) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) o;
        return new JsonObject(map);
      }
      return o;
    } catch (Exception e) {
      throw new DecodeException("Failed to decode: " + e.getMessage());
    }
  }

  @Override
  public Buffer toBuffer(Object object, boolean pretty) throws EncodeException {
    if (pretty) {
      return Buffer.buffer(toString(object, pretty));
    } else {
      return Json.encodeToBuffer(object);
    }
  }
}
