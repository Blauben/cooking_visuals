import 'package:flutter/cupertino.dart';

import 'branch_element.dart';

class Branch {
  List<BranchElement> instructions = List<BranchElement>.empty(growable: true);
  final String environment;
  Branch? next;
  List<Branch> prev = List.empty(growable: true);
  int? nextKey;
  final int id;

  Branch(this.environment, this.id);

  factory Branch.fromJson({required Map<String, dynamic> json}) {
    final environment = json["environment"] as String;
    final id = json["id"] as int;
    final nextKey = json["nextKey"] as int?;
    final instructionData = json["instructions"] as List<dynamic>?;
    final instructions = instructionData != null
        ? instructionData
            .map((data) => BranchElement.fromJson(json: data))
            .toList()
        : <Instruction>[];
    final branch = Branch(environment, id);
    branch.instructions = instructions;
    branch.nextKey = nextKey;
    return branch;
  }

  Widget graph() {
    List<Widget> branchElements =
        instructions.map((element) => element.graph()).toList();
    branchElements.insert(0, Text(environment));
    return Column(children: branchElements);
  }
}
