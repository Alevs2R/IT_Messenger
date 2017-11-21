import React, { Component } from 'react';
import {Modal, Text, TouchableHighlight, View, Button, Picker} from 'react-native';

export default class SettingsModal extends Component {

    render() {
        return (
                <Modal
                    animationType="slide"
                    transparent={false}
                    visible={this.props.visible}
                    onRequestClose={() => {this.props.setVisible(!this.props.visible)}}
                >
                    <View style={{marginTop: 22, marginHorizontal: 10}}>
                            <Text>Compression algorithm</Text>

                            <Picker
                                selectedValue={this.props.settings.compressionAlgo}
                                onValueChange={(itemValue) => this.props.setSettings({compressionAlgo: itemValue})}>
                                <Picker.Item label="Arithmetic" value="arithmetic" />
                                <Picker.Item label="LZW" value="lzw" />
                                <Picker.Item label="Run-length" value="rle" />
                            </Picker>

                            <Text>Coding algorithm</Text>
                            <Picker
                                selectedValue={this.props.settings.codingAlgo}
                                onValueChange={(itemValue) => this.props.setSettings({codingAlgo: itemValue})}>
                                <Picker.Item label="Hamming" value="hamming" />
                                <Picker.Item label="Repetition" value="repetition" />
                                <Picker.Item label="Read-Muller" value="rm" />

                            </Picker>

                             <Text>Noise level</Text>

                        <Picker
                            selectedValue={this.props.settings.noiseLevel}
                            onValueChange={(itemValue) => this.props.setSettings({noiseLevel: itemValue})}>
                            <Picker.Item label="no noise" value="0" />
                            <Picker.Item label="1%" value="1" />
                            <Picker.Item label="5%" value="5" />
                            <Picker.Item label="15%" value="15" />

                        </Picker>

                        <Button onPress={() => {
                                this.props.setVisible(!this.props.visible)
                            }} title={"OK"} />
                    </View>
                </Modal>
        );
    }
}