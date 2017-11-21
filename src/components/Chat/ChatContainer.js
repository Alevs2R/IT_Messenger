import React from 'react';
import {
    Text, Dimensions, View, TextInput, Button, ToolbarAndroid, TouchableHighlight, AsyncStorage
} from "react-native";
import { DocumentPicker, DocumentPickerUtil } from 'react-native-document-picker';
import styles from "./chatStyles";
import {Bubble, GiftedChat, Message} from "react-native-gifted-chat";
import Pusher from 'pusher-js/react-native';
import SettingsModal from "../Settings/SettingsModal";
const uuidv4 = require('uuid/v4');
import Coder from '../../utils/Coder';
var RNFS = require('react-native-fs');


const SERVER_URI = '78.155.218.30:3000';

export default class ChatContainer extends React.Component {

    state = {
        messages: [],
        showSettings: false,
        settings:{
            codingAlgo: 'hamming',
            compressionAlgo: 'arithmetic',
            noiseLevel: '0',
        },
    };

    constructor(props){
        super(props);
    }

    setSettingsVisible = (visible) => {
        this.setState({showSettings:visible})
    };

    setSettings = (settings) => {
        const newState = {settings: Object.assign({}, this.state.settings, settings)};
        this.setState(newState);
    };

    componentWillMount = async () => {

        const savedMessages = await AsyncStorage.getItem('messages');
        if(savedMessages){
            this.setState({messages: JSON.parse(savedMessages)})
        } else {
            this.setState({
                messages: [
                    {
                        _id: 1,
                        text: "You are not a good person. You know that, right? Good people don't get up here. ",
                        createdAt: new Date(),
                        user: {
                            _id: 2,
                            name: 'GlaDOS',
                        },
                    },
                ],
            });
        }

        // Enable pusher logging - don't include this in production
        Pusher.logToConsole = true;

        this.pusher = new Pusher('bbba944ae223bf0e833f', {
            cluster: 'eu',
            encrypted: true
        });

        this.chatChannel = this.pusher.subscribe('chat_channel'); // (2)
        this.chatChannel.bind('pusher:subscription_succeeded', () => { // (3)
            this.chatChannel.bind('message', this.onReceive);
        });
    }

    async downloadEncodedFile(file){
        const options = {
            fromUrl: `http://${SERVER_URI}${file.url}`,
            toFile: RNFS.DocumentDirectoryPath + '/'+uuidv4(),
        };
        const {promise} = RNFS.downloadFile(options);
        try {
            await(promise);
            return {
                uri: options.toFile,
                name: file.name
            }
        } catch(e){
            console.log(e);
        }
    }

    onReceive = async (message) => {

        console.log(message);

        if(this.state.messages.find(item => item._id === message._id)) return;

        if(message.file){

            const file = await this.decode(await this.downloadEncodedFile(message.file));

            message.text = `Initial size: ${file.initialSize}\nCompressed size: ${file.compressedSize} bytes\nEncoded size: ${file.encodedSize} bytes`;
            if (message.commonType === 'doc'){
                message.file = file;
                message.text = `File ${file.fileName}\n${message.text}`;
            } else if(message.commonType === 'img'){
                message.file = file;
                message.text = `File ${file.fileName}\n${message.text}`;
                message.image = 'file:'+file.uri;
            }
            else {
                let content;
                try{
                    content = await RNFS.readFile(file.uri);
                } catch (e){
                    content = 'INVALID MESSAGE';
                }
                message.text = `"${content}"\n${message.text}`;

            }
        }

        const messages = GiftedChat.append(this.state.messages, message);
        this.setState({messages});
        AsyncStorage.setItem('messages', JSON.stringify(messages));
    };

    encode = async (file) => {
        return await Coder.encode(uuidv4(), file.uri,  this.state.settings.compressionAlgo, this.state.settings.codingAlgo, this.state.settings.noiseLevel);
    };

    decode = async (file) => {
        const decoded = await Coder.decode(uuidv4(),file.uri,this.state.settings.codingAlgo);
        const encodedSize = decoded.encodedSize;
        const uncompressed = await Coder.decode(file.name, decoded.uri, this.state.settings.compressionAlgo);
        const compressedSize = uncompressed.encodedSize;
        const initialSize = uncompressed.decodedSize;

        return Object.assign(uncompressed, {encodedSize,compressedSize,initialSize});
    };

    onSend = async (messages = []) => {
        const file = await this.createTextFile(messages[0].text);
        const encodedFile = await this.encode(file);
        encodedFile.commonType = "txt";
        this.sendFile(encodedFile);
    };

    sendFile = file => {
        console.log(file);

        const {nickname} = this.props;
        const user = {
            _id: nickname,
            name: nickname
        };
        const data = new FormData();
        data.append('file', {
            uri: file.uri,
            type: file.type,
            name: file.fileName
        });
        data.append('commonType', file.commonType);
        data.append('user', JSON.stringify(user));
        data.append('_id', uuidv4());


            fetch(`http://${SERVER_URI}/messages/send`, {
                method: 'post',
                body: data
            }).catch(e => console.log(e));

     };

    pickDocument = (type) => {
        DocumentPicker.show({
            filetype: [type],
        },async (error,res) => {
            if(res){
                try {
                    const result = await this.encode(res);
                    result.fileName = res.fileName;
                    result.commonType = type === 'image/*' ? 'img' : 'doc';
                    console.log(result);
                    this.sendFile(result);
                } catch (e) {
                    console.error(e);
                }
            }
        });
    };

    async createTextFile(text){
        const name = uuidv4();
        const uri = RNFS.DocumentDirectoryPath + '/' + name;
        await RNFS.writeFile(uri, text, 'utf8');
        return {
            uri: 'file:'+uri,
            type: 'text/plain',
            fileName: name
        };
    }

    renderMessage(props) {
        props.currentMessage = Object.assign({}, props.currentMessage);
        if(!props.previousMessage.user || props.currentMessage.user._id !== props.previousMessage.user._id){
            props.currentMessage.text = <Text><Text style={{fontWeight: 'bold'}}>{props.currentMessage.user.name}</Text>{'\n' + props.currentMessage.text}</Text>;
        }
        if(props.currentMessage.commonType === 'doc' || props.currentMessage.commonType === 'img') {
            props.onPress = () => {
                Coder.openFile(props.currentMessage.file.uri)
            };
        }
            return (
                <Message {...props}/>
            )
        // }
    }

    onActionSelected = (action) => {
        if(action === 3) this.props.logout();
        else if (action === 1) this.pickDocument(DocumentPickerUtil.images());
        else if (action === 2) this.pickDocument(DocumentPickerUtil.allFiles());
        else if(action === 0) this.setSettingsVisible(true);
    };


    render() {
        const {nickname} = this.props;

        return (
            <View style={styles.container}>
                <ToolbarAndroid
                    title="Group chat"
                    actions={[{title: 'Settings'}, {title: 'Send image'}, {title: 'Send file'}, {title: 'Logout'}]}
                    onActionSelected={this.onActionSelected}
                    style={styles.toolbar}
                />
                <View style={styles.messages}>
                    <GiftedChat
                        messages={this.state.messages}
                        onSend={(messages) => this.onSend(messages)}
                        user={{
                            _id: nickname,
                            name: nickname
                        }}
                        renderAvatar={null}
                        renderMessage={this.renderMessage}
                    />
                </View>
                <SettingsModal settings={this.state.settings}
                               setSettings={this.setSettings}
                               visible={this.state.showSettings}
                               setVisible={this.setSettingsVisible}
                />
            </View>
        );
    }
}