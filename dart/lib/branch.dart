import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

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

  Widget graph(BuildContext context) {
    List<Widget> branchElements =
        instructions.map((element) => element.graph(context)).toList();
    return Container(
      padding: const EdgeInsets.all(10),
      margin: const EdgeInsets.fromLTRB(10, 5, 40, 10),
      decoration: BoxDecoration(
        border: Border.all(width: 2),
        borderRadius: BorderRadius.circular(8),
        color: Colors.amberAccent,
      ),
      child: Column(children: [_environment(), _branchBody(branchElements)]),
    );
  }

  Widget _environment() {
    return Text(
      environment,
      style: const TextStyle(fontWeight: FontWeight.w500),
    );
  }

  Widget _branchBody(List<Widget> branchElements) {
    return Container(
      padding: const EdgeInsets.all(10),
      decoration: BoxDecoration(
          border: Border.all(
            width: 1,
          ),
          borderRadius: BorderRadius.circular(8),
          color: const Color.fromRGBO(102, 255, 102, 1)),
      margin: const EdgeInsets.all(5),
      child: Column(children: branchElements),
    );
  }
}
