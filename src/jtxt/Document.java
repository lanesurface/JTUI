package jtxt;

import java.io.File;
import java.util.Arrays;

import jtxt.emulator.tui.Component;
import jtxt.emulator.tui.Layout;

public class Document {
  /**
   * The pages that make up this document. Each page
   */
  private Page[] pages;
  private int pageIndex;

  /**
   * The underlying file that this {@code Document} represents on the client
   * computer.
   */
  protected File file;

  /**
   * Creates a new {@code Document}, where each page has the given layout and
   * settings.
   * <p>
   * A document must have an associated file on the system, as we cannot display it
   * here. Ultimately, all documents should be persisted on disk.
   *
   * @param file
   * @param rootLayout
   * @param pageSettings
   */
  public Document(
    File file,
    Layout rootLayout,
    Page.PageSettings pageSettings)
  {
    this.file = file;

    /*
     * How are we going to deal with:
     *  1. Orienting components across multiple pages (possibly with
     *     different layouts),
     *  2. Knowing whether a page has enough room for a component,
     *  3. How to wrap components when there isn't.
     */

    Arrays.fill(pages, new Page(
      pageSettings,
      rootLayout));
  }

  public void add(Component component) { }
}
