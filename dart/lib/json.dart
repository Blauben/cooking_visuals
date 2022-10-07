import "dart:convert";
import 'package:dart/branch.dart';

class Json {

  static Branch? result;

  static List<Branch> parseJsonString(String json) {
    final List parsedJson = jsonDecode(json);
    final List<Branch> branches = parsedJson.map((dynamic) => Branch.fromJson(json: dynamic)).toList();
    linkList(branches);
    return branches;
  }

  static void linkList(List<Branch> branches) {
     for(Branch b in branches) {
      final int? indexNullable = b.nextKey;
     if (indexNullable == null) {
       continue;
     }
     b.next = locateBranch(branches, indexNullable);
     if(b.next != null) {
       b.next!.prev.add(b);
     } else {
       result = b;
     }
    }
  }

  static Branch? locateBranch(List<Branch> branches, int? index) {
    for(Branch b in branches) {
      if(b.id == index) {
        return b;
      }
    }
    return null;
  }

}
