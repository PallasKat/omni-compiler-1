package exc.openacc;

import exc.block.*;
import exc.object.*;
import java.util.*;

public class AccLoop extends AccDirective{
  //private boolean useWORKER = false;
  private List<ACCpragma> parallelismList = Arrays.asList(ACCpragma.GANG, ACCpragma.VECTOR);

  AccLoop(ACCglobalDecl decl, AccInformation info, PragmaBlock pb) {
    super(decl, info, pb);
  }

  @Override
  void analyze() throws ACCexception {
    //check parallelism
    checkParallelism();

    if(_info.hasClause(ACCpragma.SEQ)){
      return;
    }
    //if(!useWORKER && _info.hasClause(ACCpragma.WORKER) && !_info.hasClause(ACCpragma.GANG) && !_info.hasClause(ACCpragma.VECTOR)){
//      return;
//    }

    //check loop body
    int collapseNum = 1;
    Xobject collapseNumExpr = _info.getIntExpr(ACCpragma.COLLAPSE);
    if(collapseNumExpr != null){
      collapseNum = collapseNumExpr.getInt();
    }
    XobjList inductionVarList = checkCollapsedLoop(_pb.getBody().getHead(), collapseNum);
    for(Xobject inductionVar : inductionVarList){
      String symbol = inductionVar.getSym();
      ACCvar var = _info.findACCvar(symbol);
      if(var == null){
        _info.setVar(ACCpragma.PRIVATE, Xcons.Symbol(Xcode.VAR, symbol));
      }else if(! var.isPrivate()){
        throw new ACCexception(symbol + " is induction variable but not private");
      }
    }

    setVarIdents();
  }

  @Override
  void translate() throws ACCexception {

  }

  @Override
  void rewrite() throws ACCexception {
    _pb.replace(Bcons.COMPOUND(_pb.getBody()));
  }

  private void checkParallelism() throws ACCexception{
    Set<ACCpragma> inputParallelism = getParallelismSet(_info);

    if(inputParallelism.contains(ACCpragma.AUTO) && inputParallelism.size() != 1 ||
            inputParallelism.contains(ACCpragma.SEQ) && inputParallelism.size() != 1){
      throw new ACCexception("invalid parallelism");
    }
    fixInputParallelism(inputParallelism);

    Set<ACCpragma> innerParallelism = getInnerParallelism();
    Set<ACCpragma> outerParallelism = getOuterParallelism();

    ACCpragma target = getTargetParallelism();
    Set<ACCpragma> outputParallelism = EnumSet.noneOf(ACCpragma.class);
    ACCpragma innerCoarsest = getCoarsestParallelism(innerParallelism);
    if(inputParallelism.contains(ACCpragma.AUTO)){
      if(innerCoarsest == target){
        outputParallelism.add(ACCpragma.SEQ);
      }else if(innerCoarsest == ACCpragma.SEQ){
        Set<ACCpragma> restParallelism = getFullParallelism();
        restParallelism.removeAll(outerParallelism);
        outputParallelism.addAll(restParallelism);
      }else{
        if(target == ACCpragma.VECTOR){
          outputParallelism.add(ACCpragma.SEQ);
        }else {
          outputParallelism.add(target);
        }
      }
    }else{
      outputParallelism.addAll(inputParallelism);
    }

    for(ACCpragma p : outputParallelism){
      if(! _info.hasClause(p)) {
        _info.setBool(p);
      }
    }
  }

  private Set<ACCpragma> getFullParallelism(){
    //return EnumSet.of(ACCpragma.GANG, ACCpragma.WORKER, ACCpragma.VECTOR);
    return EnumSet.copyOf(parallelismList);
  }

  private Set<ACCpragma> getInnerParallelism() {
    EnumSet<ACCpragma> innerParallelism = EnumSet.noneOf(ACCpragma.class);
    BlockIterator blockIterator = new topdownBlockIterator(_pb.getBody());
    for(blockIterator.init(); !blockIterator.end(); blockIterator.next()){
      Block b = blockIterator.getBlock();
      if(b.Opcode() != Xcode.ACC_PRAGMA) continue;
      AccDirective directive = (AccDirective)b.getProp(AccDirective.prop);
      AccInformation info = directive.getInfo(); //(AccInformation)b.getProp(AccInformation.prop);
      if(info.getPragma() != ACCpragma.LOOP) continue;
      innerParallelism.addAll(getParallelismSet(info));
    }
    if(innerParallelism.size() > 1 && innerParallelism.contains(ACCpragma.AUTO)){
      innerParallelism.remove(ACCpragma.AUTO);
    }
    return innerParallelism;
  }

  static private Set<ACCpragma> getParallelismSet(AccInformation info){
    EnumSet<ACCpragma> parallelismSet = EnumSet.noneOf(ACCpragma.class);
    if(info.hasClause(ACCpragma.GANG)) parallelismSet.add(ACCpragma.GANG);
    if(info.hasClause(ACCpragma.WORKER)) parallelismSet.add(ACCpragma.WORKER);
    if(info.hasClause(ACCpragma.VECTOR)) parallelismSet.add(ACCpragma.VECTOR);
    if(info.hasClause(ACCpragma.SEQ)) parallelismSet.add(ACCpragma.SEQ);

    if(parallelismSet.isEmpty()){
      parallelismSet.add(ACCpragma.AUTO);
    }
    return parallelismSet;
  }

  private Set<ACCpragma> getOuterParallelism() {
    return getOuterParallelism(_pb);
  }

  static Set<ACCpragma> getOuterParallelism(Block block){
    EnumSet<ACCpragma> outerParallelism = EnumSet.noneOf(ACCpragma.class);
    for(Block b = block.getParentBlock(); b != null; b = b.getParentBlock()){
      if(b.Opcode() != Xcode.ACC_PRAGMA) continue;
      AccDirective directive = (AccDirective)b.getProp(AccDirective.prop);
      AccInformation info = directive.getInfo(); //(AccInformation)b.getProp(AccInformation.prop);
      if(! info.getPragma().isLoop()){
        if(info.getPragma().isCompute()){
          break;
        }
        continue;
      }
      outerParallelism.addAll(getParallelismSet(info));
    }
    return outerParallelism;
  }

  private ACCpragma getTargetParallelism(){
    Set<ACCpragma> outerParallelism = getOuterParallelism();
    Set<ACCpragma> restParallelism = EnumSet.of(ACCpragma.GANG, ACCpragma.WORKER, ACCpragma.VECTOR);
    restParallelism.removeAll(outerParallelism);
    return getCoarsestParallelism(restParallelism);
  }

  private ACCpragma getCoarsestParallelism(Set<ACCpragma> set){
    if(set.contains(ACCpragma.AUTO)) return ACCpragma.AUTO;

    for(ACCpragma p : parallelismList){
      if(set.contains(p)) return p;
    }
    return ACCpragma.SEQ;
  }

  private void fixInputParallelism(Set<ACCpragma> input){
    if(input.size() < 2) return;

    ACCpragma coarsest = getCoarsestParallelism(input);

    LinkedList<ACCpragma> list = new LinkedList<ACCpragma>(parallelismList);
    Collections.reverse(list);

    Iterator<ACCpragma> iter = list.iterator();
    while(iter.hasNext()){
      ACCpragma p = iter.next();
      if(input.contains(p)) break;
    }
    while(iter.hasNext()){
      ACCpragma p = iter.next();
      if(p == coarsest) break;
      input.add(p);
    }
  }

  private XobjList checkCollapsedLoop(Block block, int num_collapse) throws ACCexception{
    if(block.Opcode() != Xcode.FOR_STATEMENT){
      throw new ACCexception("lack of nested loops");
    }

    CforBlock forBlock = (CforBlock)block;
    if(! forBlock.isCanonical()){
      forBlock.Canonicalize();
      if(! forBlock.isCanonical()){
        throw new ACCexception("loop is non-canonical");
      }
    }

    if(num_collapse < 2){
      return Xcons.List(forBlock.getInductionVar());
    }

    BlockList forBody = forBlock.getBody();
    if(! forBody.isSingle()){
      throw new ACCexception("non tightly nested loop");
    }
    if(forBody.getIdentList() != null && ! forBody.getIdentList().isEmpty()){
      throw new ACCexception("no var declaration is allowed between nested loops");
    }
    XobjList inductionVarList = checkCollapsedLoop(forBody.getHead(), num_collapse - 1);
    inductionVarList.cons(forBlock.getInductionVar());
    return inductionVarList;
  }

  public static boolean isAcceptableClause(ACCpragma clauseKind){
    switch (clauseKind) {
    case GANG:
    case WORKER:
    case VECTOR:
    case COLLAPSE:
    case SEQ:
    case INDEPENDENT:
    case PRIVATE:
      return true;
    default:
      return clauseKind.isReduction();
    }
  }
}
