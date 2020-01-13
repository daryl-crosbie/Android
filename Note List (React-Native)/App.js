import React, { useState } from "react";
import { StyleSheet, View, FlatList, Button, Modal } from "react-native";
import Note from "./components/Note";
import InputItem from "./components/InputItem";
import uuid from "uuid/v1";

export default function App() {
  const [noteList, setNoteList] = useState([]);
  const [addMode, setAddMode] = useState(false);

  const addInputHandler = note => {
    setNoteList(currentList => [...currentList, { id: uuid(), value: note }]);
    setAddMode(false);
  };
  const delItemHandler = note => {
    setNoteList(currentList => {
      return currentList.filter(item => item.id !== note);
    });
  };
  const cancelHandler = () => {
    setAddMode(false);
  };

  return (
    <View style={styles.root}>
      <Button
        title="Add New Note"
        onPress={() => {
          setAddMode(true);
        }}
      />
      <InputItem
        show={addMode}
        cancel={cancelHandler}
        inputHandler={addInputHandler}
      />
      <FlatList
        data={noteList}
        keyExtractor={(item, index) => item.id}
        renderItem={itemData => (
          <Note
            id={itemData.item.id}
            title={itemData.item.value}
            delHandler={delItemHandler}
          />
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  root: {
    padding: 30
  }
});
