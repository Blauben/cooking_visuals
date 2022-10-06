import "dart:convert";

import 'package:cooking_graph_app/branch.dart';

class Json {

  static List<Branch> parseJsonString(String json) {
    final List parsedJson = jsonDecode(json);
    final List<Branch> branches = parsedJson.map((dynamic) => Branch.fromJson(json: dynamic)).toList();
    linkList(branches);
    return branches;
  }

  static void linkList(List<Branch> branches) { //TODO check if Java behavior applies here
    for(Branch b in branches) {
      final int? indexNullable = b.nextKey;
     if (indexNullable == null) {
       continue;
     }
     final int index = indexNullable;
     b.next = locateBranch(branches, index);
     if(b.next != null) {
       b.next!.prev = b;
     }
    }
  }

  static Branch? locateBranch(List<Branch> branches, int index) {
    for(Branch b in branches) {
      if(b.nextKey == index) {
        return b;
      }
    }
    return null;
  }

}
