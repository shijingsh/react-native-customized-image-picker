import React, {Component} from 'react';
import {
    View, Text, StyleSheet, ScrollView,
    Image, TouchableOpacity, NativeModules, Dimensions
} from 'react-native';


import ImagePicker from 'react-native-customized-image-picker';

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center'
    },
    button: {
        backgroundColor: 'blue',
        marginBottom: 10
    },
    text: {
        color: 'white',
        fontSize: 20,
        textAlign: 'center'
    }
});

export default class App extends Component {

    constructor() {
        super();
        this.state = {
            images: null
        };
    }

    pickSingle(cropit) {
        ImagePicker.openPicker({
            width: 300,
            height: 300,
            cropping: cropit,
            isCamera:true
        }).then(images => {
            this.setState({
                images: images.map(i => {
                    console.log('received image', i);
                    return {uri: i.path, width: i.width, height: i.height, mime: i.mime};
                })
            });
        }).catch(e => {
            console.log(e.code);
            alert(e);
        });
    }

    renderImage(image) {
        return <Image style={{width: 300, height: 300, resizeMode: 'contain'}} source={image} />
    }

    renderAsset(image) {

        return this.renderImage(image);
    }

    render() {
        return <View style={styles.container}>
          <ScrollView>
              {this.state.images ? this.state.images.map(i => <View key={i.uri}>{this.renderAsset(i)}</View>) : null}
          </ScrollView>

          <TouchableOpacity onPress={() => this.pickSingle(false)} style={styles.button}>
            <Text style={styles.text}>Select Single</Text>
          </TouchableOpacity>
          <TouchableOpacity onPress={() => this.pickSingle(true)} style={styles.button}>
            <Text style={styles.text}>Select Single With Cropping</Text>
          </TouchableOpacity>
        </View>;
    }
}
