/*
 * Ext GWT 2.2.0 - Ext for GWT
 * Copyright(c) 2007-2010, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package com.extjs.gxt.ui.client.widget.treegrid;

import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.grid.BufferView;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid.TreeNode;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.Joint;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings( {"unchecked", "rawtypes"})
public class TreeGridView extends BufferView {

  protected TreeGrid tree;
  protected TreeStore treeStore;

  private int treeColumn = -1;

  public TreeGridView() {
    setRowSelectorDepth(20);
    setCellSelectorDepth(10);
  }

  public void collapse(TreeNode node) {
    ModelData p = node.m;
    ModelData lc = treeStore.getLastChild(p);

    int start = ds.indexOf(p);
    int end = tree.findLastOpenChildIndex(lc);

    if (GXT.isAriaEnabled()) {
      getRow(start).setAttribute("aria-expanded", "false");
    }

    for (int i = end; i > start; i--) {
      ds.remove(i);
    }
    tree.refresh(node.m);
  }

  public void expand(TreeNode node) {
    ModelData p = node.m;
    List<ModelData> children = treeStore.getChildren(p);
    int idx = ds.indexOf(p);

    ds.insert(children, idx + 1);

    if (GXT.isAriaEnabled()) {
      getRow(idx).setAttribute("aria-expanded", "true");
    }

    for (ModelData child : children) {
      TreeNode cn = tree.findNode(child);
      if (cn.isExpanded()) {
        expand(cn);
      }
    }
    tree.refresh(node.m);
  }

  public Element getJointElement(TreeNode node) {
    if (node.joint == null) {
      Element row = getRowElement(node);
      if (row != null) {
        El jointEl = fly(row).selectNode(".x-tree3-el");
        if (jointEl != null && widgetList.size() > 0) {
          El j = jointEl.selectNode(".x-tree3-el-jnt");
          if (j != null) {
            node.joint = j.dom.getFirstChild().cast();
          }
        }
        if (node.joint == null) {

          node.joint = jointEl == null ? null : (Element) jointEl.dom.getChildNodes().getItem(1);
        }
      }
    }
    return node.joint;
  }

  public String getTemplate(ModelData m, String id, String text, AbstractImagePrototype icon, boolean checkable,
      Joint joint, int level) {

    StringBuffer sb = new StringBuffer();
    sb.append("<div role=\"presentation\" unselectable=\"on\" id=\"");
    sb.append(id);
    sb.append("\" class=\"x-tree3-node\">");

    String cls = "x-tree3-el";
    if (GXT.isHighContrastMode) {
      switch (joint) {
        case COLLAPSED:
          cls += " x-tree3-node-joint-collapse";
          break;
        case EXPANDED:
          cls += " x-tree3-node-joint-expand";
          break;
      }
    }

    sb.append("<div role=\"presentation\" unselectable=\"on\" class=\"" + cls + "\">");

    String h = "";
    switch (joint) {
      case COLLAPSED:
        h = tree.getStyle().getJointCollapsedIcon().getHTML();
        break;
      case EXPANDED:
        h = tree.getStyle().getJointExpandedIcon().getHTML();
        break;
      default:
        h = "<img src=\"" + GXT.BLANK_IMAGE_URL + "\" style='width: 16px'>";
    }

    sb.append("<img src=\"");
    sb.append(GXT.BLANK_IMAGE_URL);
    sb.append("\" style=\"height: 18px; width: ");
    sb.append(level * 18);
    sb.append("px;\" />");
    sb.append(h);
    if (checkable) {
      sb.append(GXT.IMAGES.unchecked().getHTML());
    } else {
      sb.append("<span></span>");
    }
    if (icon != null) {
      sb.append(icon.getHTML());
    } else {
      sb.append("<span></span>");
    }
    sb.append("<span unselectable=\"on\" class=\"x-tree3-node-text\">");
    sb.append(text);
    sb.append("</span>");

    sb.append("</div>");
    sb.append("</div>");

    return sb.toString();
  }

  public String getWidgetTemplate(ModelData m, String id, String text, AbstractImagePrototype icon, boolean checkable,
      Joint joint, int level) {

    StringBuffer sb = new StringBuffer();
    sb.append("<div unselectable=\"on\" id=\"");
    sb.append(id);
    sb.append("\" class=\"x-tree3-node\">");
    // jumping content when inserting in column with cell widget the column
    // extra width fixes
    sb.append("<div role=\"presentation\" unselectable=\"on\" class=\"x-tree3-el\" style=\"width: 1000px;height: auto;\">");

    sb.append("<table cellpadding=0 cellspacing=0 role=presentation><tr role=presentation><td role=presentation>");

    String h = "";
    switch (joint) {
      case COLLAPSED:
        h = tree.getStyle().getJointCollapsedIcon().getHTML();
        break;
      case EXPANDED:
        h = tree.getStyle().getJointExpandedIcon().getHTML();
        break;
      default:
        h = "<img src=\"" + GXT.BLANK_IMAGE_URL + "\" style='width: 16px'>";
    }

    sb.append("</td><td><img src=\"");
    sb.append(GXT.BLANK_IMAGE_URL);
    sb.append("\" style=\"height: 18px; width: ");
    sb.append(level * 18);
    sb.append("px;\" /></td><td  class='x-tree3-el-jnt'>");
    sb.append(h);
    if (checkable) {
      sb.append(GXT.IMAGES.unchecked().getHTML());
    } else {
      sb.append("<span></span>");
    }
    sb.append("</td><td>");
    if (icon != null) {
      sb.append(icon.getHTML());
    } else {
      sb.append("<span></span>");
    }
    sb.append("</td><td>");
    sb.append("<span unselectable=\"on\" class=\"x-tree3-node-text\">");
    sb.append(text);
    sb.append("</span>");
    sb.append("</td></tr></table>");

    sb.append("</div>");
    sb.append("</div>");

    return sb.toString();
  }

  public boolean isSelectableTarget(ModelData model, Element target) {
    TreeNode node = tree.findNode(model);
    if (node != null) {

      Element j = getJointElement(node);
      if (j != null && DOM.isOrHasChild(j, target)) {
        return false;
      }
    }
    return true;
  }

  public void onIconStyleChange(TreeNode node, AbstractImagePrototype icon) {
    Element iconEl = node.icon;
    if (iconEl == null) {

      Element rowEl = getRowElement(node);
      if (rowEl != null) {
        El nodeEl = fly(rowEl).selectNode(".x-tree3-el");
        if (nodeEl != null) {
          iconEl = nodeEl.dom.getChildNodes().getItem(3).cast();

        }
      }
    }
    if (iconEl != null) {
      if (icon != null) {
        node.icon = (Element) iconEl.getParentElement().insertBefore(icon.createElement(), iconEl);
      } else {
        node.icon = (Element) iconEl.getParentElement().insertBefore(DOM.createSpan(), iconEl);
      }
      El.fly(iconEl).remove();
    }
  }

  public void onJointChange(TreeNode node, Joint joint) {
    Element jointEl = getJointElement(node);
    if (jointEl != null) {
      switch (joint) {
        case EXPANDED:
          node.joint = (Element) jointEl.getParentElement().insertBefore(
              tree.getStyle().getJointExpandedIcon().createElement(), jointEl);
          if (GXT.isHighContrastMode) {
            El.fly(jointEl.getParentElement()).addStyleName("x-tree3-node-joint-expand").removeStyleName(
                "x-tree3-node-joint-collapse");
          }
          break;
        case COLLAPSED:
          node.joint = (Element) jointEl.getParentElement().insertBefore(
              tree.getStyle().getJointCollapsedIcon().createElement(), jointEl);
          if (GXT.isHighContrastMode) {
            El.fly(jointEl.getParentElement()).addStyleName("x-tree3-node-joint-collapse").removeStyleName(
                "x-tree3-node-joint-expand");
          }
          break;
        default:
          node.joint = (Element) jointEl.getParentElement().insertBefore(
              XDOM.create("<img src=\"" + GXT.BLANK_IMAGE_URL + "\" style='width: 16px'>"), jointEl);
          if (GXT.isHighContrastMode) {
            El.fly(jointEl.getParentElement()).removeStyleName("x-tree3-node-joint-collapse").removeStyleName(
                "x-tree3-node-joint-expand");
          }
      }
      El.fly(jointEl).remove();
    }
  }

  public void onLoading(TreeNode node) {
    onIconStyleChange(node, IconHelper.createStyle("x-tree3-loading"));
  }

  @Override
  protected void cleanModel(ModelData at) {
    TreeNode node = tree.findNode(at);
    if (node != null) {
      node.clearElements();
    }
  }

  @Override
  protected void doSort(int colIndex, SortDir sortDir) {
    treeStore.sort(cm.getDataIndex(colIndex), sortDir);
  }

  @Override
  protected String getRenderedValue(ColumnData data, int rowIndex, int colIndex, ModelData m, String property) {
    GridCellRenderer<ModelData> r = cm.getRenderer(colIndex);
    List<Widget> rowMap = widgetList.get(rowIndex);
    rowMap.add(colIndex, null);
    if (r != null) {
      Object o = r.render(ds.getAt(rowIndex), property, data, rowIndex, colIndex, ds, grid);
      if ((o instanceof Widget && !(r instanceof WidgetTreeGridCellRenderer))  || r instanceof WidgetTreeGridCellRenderer) {
        Widget w = null;
        if (o instanceof Widget) {
          w = (Widget) o;
        } else {
          w = ((WidgetTreeGridCellRenderer) r).getWidget(ds.getAt(rowIndex), property, data, rowIndex, colIndex, ds,
              grid);
        }

        rowMap.set(colIndex, w);
        if (colIndex == treeColumn) {
          return o.toString();
        }
        return "";
      } else {
        return o.toString();
      }
    }
    Object val = m.get(property);

    ColumnConfig c = cm.getColumn(colIndex);

    if (val != null && c.getNumberFormat() != null) {
      Number n = (Number) val;
      NumberFormat nf = cm.getColumn(colIndex).getNumberFormat();
      val = nf.format(n.doubleValue());
    } else if (val != null && c.getDateTimeFormat() != null) {
      DateTimeFormat dtf = c.getDateTimeFormat();
      val = dtf.format((Date) val);
    }

    String text = null;
    if (val != null) {
      text = val.toString();
    }
    return Util.isEmptyString(text) ? "&#160;" : text;
  }

  protected Element getRowElement(TreeNode node) {
    return (Element) getRow(ds.indexOf(node.m));
  }

  @Override
  protected SortInfo getSortState() {
    return treeStore.getSortState();
  }

  protected Element getWidgetCell(int row, int col) {
    if (col == treeColumn) {
      Element cell = (Element) getCell(row, col);
      if (cell != null) {
        cell = El.fly(cell).selectNode(".x-tree3-node-text").dom;
        cell.setAttribute(GXT.isIE ? "className" : "class", "x-tree3-node-text x-tree3-node-text-widget");
        cell.getParentElement().getStyle().setProperty("padding", "2px 0px 2px 4px");
        return cell;
      }
    }
    return super.getWidgetCell(row, col).cast();
  }

  @Override
  protected void init(Grid grid) {
    super.init(grid);
    tree = (TreeGrid) grid;
    treeStore = tree.getTreeStore();
    selectable = !grid.isDisableTextSelection();
  }

  @Override
  protected void initData(ListStore ds, ColumnModel cm) {
    super.initData(ds, cm);
    treeColumn = -1;
    List<ColumnConfig> l = cm.getColumns();
    for (int i = 0; i < l.size(); i++) {
      ColumnConfig c = l.get(i);
      GridCellRenderer r = c.getRenderer();
      if (r != null && r instanceof TreeGridCellRenderer) {
        assert treeColumn == -1 : "You may only specify one TreeGridCellRenderer";
        treeColumn = i;
      }
    }
    assert treeColumn != -1 : "No TreeGridCellRenderer specified";
  }

  @Override
  protected void insertRows(ListStore<ModelData> store, int firstRow, int lastRow, boolean isUpdate) {
    super.insertRows(store, firstRow, lastRow, isUpdate);
    if (GXT.isAriaEnabled()) {
      for (int i = firstRow; i <= lastRow; i++) {
        ModelData m = store.getAt(i);
        getRow(i).setAttribute("aria-level", "" + treeStore.getDepth(m));
      }
    }
  }

  @Override
  protected void onClick(GridEvent<ModelData> ce) {
    if (ce.getModel() != null && !isSelectableTarget(ce.getModel(), ce.getTarget())) {
      return;
    }
    super.onClick(ce);
  }

  @Override
  protected void onRemove(ListStore<ModelData> ds, ModelData m, int index, boolean isUpdate) {
    super.onRemove(ds, m, index, isUpdate);
    TreeNode node = tree.findNode(m);
    if (node != null) {
      node.clearElements();
    }
  }

  @Override
  protected void onRowSelect(int rowIndex) {
    super.onRowSelect(rowIndex);
    tree.setExpanded(treeStore.getParent(tree.getStore().getAt(rowIndex)), true);
  }

}
