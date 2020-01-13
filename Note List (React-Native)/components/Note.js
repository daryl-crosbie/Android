import React from "react";
import { StyleSheet, Text, View, TouchableOpacity } from "react-native";

const Note = props => {
  return (
    <TouchableOpacity onPress={props.delHandler.bind(this, props.id)}>
      <View style={styles.listItem}>
        <Text>{props.title}</Text>
      </View>
    </TouchableOpacity>
  );
};

export default Note;

const styles = StyleSheet.create({
  listItem: {
    borderColor: "black",
    marginVertical: 10,
    padding: 10,
    backgroundColor: "#ccc",
    borderWidth: 1
  }
});
