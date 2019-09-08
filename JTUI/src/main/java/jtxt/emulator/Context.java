/*
 * Copyright 2018, 2019 Lane W. Surface
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jtxt.emulator;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import jtxt.emulator.util.InitializationReader;

/**
 * Encapsulates the properties of an instance of {@code Terminal}. Simple rendering
 * hints, like the font size and family, as well as the number of lines and columns
 * in the terminal are specified using a {@code Configuration} object, and passed to
 * the {@code Terminal} constructor.
 */
public class Context implements ResizeSubject {
  private int lineSize, numLines;

  /**
   * The size of the text-pane. This is calculated from the size of a glyph and the
   * number of lines and columns.
   */
  Dimension windowSize;

  /**
   * All of the objects which wish to be notified when the dimensions of the text
   * interface are changed.
   */
  private List<ResizeSubscriber> resizeSubscribers;

  /**
   * The bounds of the root container of the terminal.
   */
  private Region bounds;

  /**
   * The number of times per second that the terminal should poll for updates to
   * state in the window.
   */
  final int updatesPerSecond;

  /**
   * Constructs a new {@code Configuration} object with properties identical to
   * config.
   *
   * @param context The {@code Configuration} to replicate.
   */
  public Context(Context context) {
    this(
      context.lineSize,
      context.numLines);
    /*
     *  Make sure the dimensions are copied into the new context; this
     *  is used for returning context instance from the terminal, and
     *  rendering is based on how these may be defined.
     */
    windowSize = context.windowSize;
  }

  /**
   * @param lineSize The number of characters available on each line.
   * @param numLines The number of lines in the terminal.
   */
  public Context(
    int lineSize,
    int numLines)
  {
    resizeSubscribers = new ArrayList<>();
    this.lineSize = lineSize;
    this.numLines = numLines;
    bounds = new Region(
      0,
      0,
      numLines,
      lineSize);
    updatesPerSecond = 60;
  }

  /**
   * Constructs a configuration for the given initialization file. See the
   * documentation for details about property values and their respective format in
   * the initialization file.
   *
   * @param filename An initialization file for this context, given appropriate
   *   key=value pairs for the emulator.
   */
  public Context(String filename) {
    InitializationReader reader = new InitializationReader(filename);

    lineSize = reader.getValueAsInt("num_chars_x");
    numLines = reader.getValueAsInt("num_chars_y");

    resizeSubscribers = new ArrayList<>();
    this.updatesPerSecond = 60; // TODO: Replace default value.
  }

  public void setDimensions(
    int numLines,
    int lineSize,
    int width,
    int height)
  {
    this.numLines = numLines;
    this.lineSize = lineSize;
    windowSize.width = width;
    windowSize.height = height;

    resized();
  }

  public int getNumberOfLines() {
    return numLines;
  }

  public int getLineSize() {
    return lineSize;
  }

  public Region getBounds() {
    return bounds;
  }

  public int getWidth() {
    return windowSize.width;
  }

  public int getHeight() {
    return windowSize.height;
  }

  @Override
  public void subscribe(ResizeSubscriber subscriber) {
    resizeSubscribers.add(subscriber);
  }

  @Override
  public void remove(ResizeSubscriber subscriber) {
    resizeSubscribers.remove(subscriber);
  }

  @Override
  public void resized() {
    bounds = new Region(
      0,
      0,
      numLines,
      lineSize);

    for (ResizeSubscriber subscriber : resizeSubscribers)
      subscriber.resize(
        numLines,
        lineSize);
  }
}
