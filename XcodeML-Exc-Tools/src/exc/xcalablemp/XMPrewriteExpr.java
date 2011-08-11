/*
 * $TSUKUBA_Release: $
 * $TSUKUBA_Copyright:
 *  $
 */

package exc.xcalablemp;

import exc.block.*;
import exc.object.*;

public class XMPrewriteExpr {
  private XMPglobalDecl		_globalDecl;

  public XMPrewriteExpr(XMPglobalDecl globalDecl) {
    _globalDecl = globalDecl;
  }

  public void rewrite(FuncDefBlock def) {
    FunctionBlock fb = def.getBlock();
    if (fb == null) return;

    // get symbol table
    XMPsymbolTable localXMPsymbolTable = XMPlocalDecl.declXMPsymbolTable(fb);

    // rewrite parameters
    rewriteParams(fb, localXMPsymbolTable);

    // rewrite Function Exprs
    rewriteFuncExprs(fb, localXMPsymbolTable);

    // create local object descriptors, constructors and desctructors
    XMPlocalDecl.setupObjectId(fb);
    XMPlocalDecl.setupConstructor(fb);
    XMPlocalDecl.setupDestructor(fb);

    def.Finalize();
  }

  private void rewriteParams(FunctionBlock funcBlock, XMPsymbolTable localXMPsymbolTable) {
    XobjList identList = funcBlock.getBody().getIdentList();
    if (identList == null) {
      return;
    } else {
      for(Xobject x : identList) {
        Ident id = (Ident)x;
        XMPalignedArray alignedArray = localXMPsymbolTable.getXMPalignedArray(id.getName());
        if (alignedArray != null) {
          id.setType(Xtype.Pointer(alignedArray.getType()));
        }
      }
    }
  }

  private void rewriteFuncExprs(FunctionBlock funcBlock, XMPsymbolTable localXMPsymbolTable) {
    BasicBlockExprIterator iter = new BasicBlockExprIterator(funcBlock);
    for (iter.init(); !iter.end(); iter.next()) {
      Xobject expr = iter.getExpr();

      try {
        rewriteExpr(expr, localXMPsymbolTable);
      } catch (XMPexception e) {
        XMP.error(expr.getLineNo(), e.getMessage());
      }
    }
  }

  private void rewriteExpr(Xobject expr, XMPsymbolTable localXMPsymbolTable) throws XMPexception {
    if (expr == null) return;

    bottomupXobjectIterator iter = new bottomupXobjectIterator(expr);
    iter.init();
    while (!iter.end()) {
      Xobject newExpr = null;
      Xobject myExpr = iter.getXobject();
      if (myExpr == null) {
        iter.next();
        continue;
      }

      switch (myExpr.Opcode()) {
        case ARRAY_REF:
          {
            String arrayName = myExpr.getSym();
            XMPalignedArray alignedArray = findXMPalignedArray(arrayName, localXMPsymbolTable);
            if (alignedArray != null) {
              if (alignedArray.checkRealloc()) {
                iter.next();
                rewriteAlignedArrayExpr(iter, alignedArray);
                break;
              }
            }

            iter.next();
            break;
          }
        // XXX delete this
        case CO_ARRAY_REF:
          {
            System.out.println(myExpr.toString());
            break;
          }
        default:
          iter.next();
      }
    }
  }

  private void rewriteAlignedArrayExpr(bottomupXobjectIterator iter,
                                       XMPalignedArray alignedArray) throws XMPexception {
    XobjList getAddrFuncArgs = Xcons.List(alignedArray.getAddrId().Ref());
    parseArrayExpr(iter, alignedArray, 0, getAddrFuncArgs);
  }

  private void parseArrayExpr(bottomupXobjectIterator iter,
                              XMPalignedArray alignedArray, int arrayDimCount, XobjList args) throws XMPexception {
    String syntaxErrMsg = "syntax error on array expression, cannot rewrite distributed array";
    Xobject prevExpr = iter.getPrevXobject();
    Xcode prevExprOpcode = prevExpr.Opcode();
    Xobject myExpr = iter.getXobject();
    Xobject parentExpr = iter.getParent();
    switch (myExpr.Opcode()) {
      case PLUS_EXPR:
        {
          switch (prevExprOpcode) {
            case ARRAY_REF:
              {
                if (arrayDimCount != 0)
                  throw new XMPexception(syntaxErrMsg);

                break;
              }
            case POINTER_REF:
              break;
            default:
              throw new XMPexception(syntaxErrMsg);
          }

          if (parentExpr.Opcode() == Xcode.POINTER_REF) {
            args.add(getCalcIndexFuncRef(alignedArray, arrayDimCount, myExpr.right()));
            iter.next();
            parseArrayExpr(iter, alignedArray, arrayDimCount + 1, args);
          }
          else {
            if (alignedArray.getDim() == arrayDimCount) {
              Xobject funcCall = createRewriteAlignedArrayFunc(alignedArray, arrayDimCount, args, Xcode.POINTER_REF);
              myExpr.setLeft(funcCall);
            }
            else {
              args.add(getCalcIndexFuncRef(alignedArray, arrayDimCount, myExpr.right()));
              Xobject funcCall = createRewriteAlignedArrayFunc(alignedArray, arrayDimCount + 1, args, Xcode.PLUS_EXPR);
              iter.setXobject(funcCall);
            }

            iter.next();
          }

          return;
        }
      case POINTER_REF:
        {
          switch (prevExprOpcode) {
            case PLUS_EXPR:
              break;
            default:
              throw new XMPexception(syntaxErrMsg);
          }

          iter.next();
          parseArrayExpr(iter, alignedArray, arrayDimCount, args);
          return;
        }
      default:
        {
          switch (prevExprOpcode) {
            case ARRAY_REF:
              {
                if (arrayDimCount != 0)
                  throw new XMPexception(syntaxErrMsg);

                break;
              }
            case PLUS_EXPR:
            case POINTER_REF:
              break;
            default:
              throw new XMPexception(syntaxErrMsg);
          }

          Xobject funcCall = createRewriteAlignedArrayFunc(alignedArray, arrayDimCount, args, prevExprOpcode);
          iter.setPrevXobject(funcCall);
          return;
        }
    }
  }

  private Xobject createRewriteAlignedArrayFunc(XMPalignedArray alignedArray, int arrayDimCount,
                                                XobjList getAddrFuncArgs, Xcode opcode) throws XMPexception {
    int arrayDim = alignedArray.getDim();
    Ident getAddrFuncId = null;
    if (arrayDim == arrayDimCount) {
      getAddrFuncId = XMP.getMacroId("_XMP_M_GET_ADDR_E_" + arrayDim, Xtype.Pointer(alignedArray.getType()));
      for (int i = 0; i < arrayDim - 1; i++)
        getAddrFuncArgs.add(alignedArray.getAccIdAt(i).Ref());
    }
    else {
      getAddrFuncId = XMP.getMacroId("_XMP_M_GET_ADDR_" + arrayDimCount, Xtype.Pointer(alignedArray.getType()));
      for (int i = 0; i < arrayDimCount; i++)
        getAddrFuncArgs.add(alignedArray.getAccIdAt(i).Ref());
    }

    Xobject retObj = getAddrFuncId.Call(getAddrFuncArgs);
    switch (opcode) {
      case ARRAY_REF:
      case PLUS_EXPR:
        return retObj;
      case POINTER_REF:
        if (arrayDim == arrayDimCount) {
          return Xcons.List(Xcode.POINTER_REF, retObj.Type(), retObj);
        }
        else {
          return retObj;
        }
      default:
        throw new XMPexception("unknown operation in exc.xcalablemp.XMPrewrite.createRewriteAlignedArrayFunc()");
    }
  }

  private Xobject getCalcIndexFuncRef(XMPalignedArray alignedArray, int index, Xobject indexRef) throws XMPexception {
    switch (alignedArray.getAlignMannerAt(index)) {
      case XMPalignedArray.NOT_ALIGNED:
      case XMPalignedArray.DUPLICATION:
        return indexRef;
      case XMPalignedArray.BLOCK:
        if (alignedArray.hasShadow()) {
          XMPshadow shadow = alignedArray.getShadowAt(index);
          switch (shadow.getType()) {
            case XMPshadow.SHADOW_NONE:
            case XMPshadow.SHADOW_NORMAL:
              {
                XobjList args = Xcons.List(indexRef,
                                           alignedArray.getGtolTemp0IdAt(index).Ref());
                return XMP.getMacroId("_XMP_M_CALC_INDEX_BLOCK").Call(args);
              }
            case XMPshadow.SHADOW_FULL:
              return indexRef;
            default:
              throw new XMPexception("unknown shadow type");
          }
        }
        else {
          XobjList args = Xcons.List(indexRef,
                                     alignedArray.getGtolTemp0IdAt(index).Ref());
          return XMP.getMacroId("_XMP_M_CALC_INDEX_BLOCK").Call(args);
        }
      case XMPalignedArray.CYCLIC:
        if (alignedArray.hasShadow()) {
          XMPshadow shadow = alignedArray.getShadowAt(index);
          switch (shadow.getType()) {
            case XMPshadow.SHADOW_NONE:
              {
                XobjList args = Xcons.List(indexRef,
                                           alignedArray.getGtolTemp0IdAt(index).Ref());
                return XMP.getMacroId("_XMP_M_CALC_INDEX_CYCLIC").Call(args);
              }
            case XMPshadow.SHADOW_FULL:
              return indexRef;
            case XMPshadow.SHADOW_NORMAL:
              throw new XMPexception("only block distribution allows shadow");
            default:
              throw new XMPexception("unknown shadow type");
          }
        }
        else {
          XobjList args = Xcons.List(indexRef,
                                     alignedArray.getGtolTemp0IdAt(index).Ref());
          return XMP.getMacroId("_XMP_M_CALC_INDEX_CYCLIC").Call(args);
        }
      default:
        throw new XMPexception("unknown align manner for array '" + alignedArray.getName()  + "'");
    }
  }

  private XMPalignedArray findXMPalignedArray(String arrayName, XMPsymbolTable localXMPsymbolTable) throws XMPexception {
    XMPalignedArray a = localXMPsymbolTable.getXMPalignedArray(arrayName);
    if (a == null) {
      a = _globalDecl.getXMPalignedArray(arrayName);
    }

    return a;
  }
}
