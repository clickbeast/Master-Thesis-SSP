
const matrixDataV2 = {
    center: {
        data:  [
            [0, {value:1, highlight: true,}, 0, 0, 0, 1, 0, 0, 0, 0],
            [1, 0, 1, 0, 0, 0, 0, 0, 1, 0],
            [0, 0, 0, 1, 0, 0, 1, 0, 0, 0],
            [0, {value:1, highlight: true, layout: {
                    background: "#24c70b"
                }}, 0, 1, 0, 0, 0, 1, 0, 0],
            [0, 1, 1, 0, 1, 0, 1, 0, 0, 0],
            [0, 1, 1, 0, 1, 0, 0, 0, 1, 0],
            [0, 1, 0, 1, 0, {value:0, highlight: true}, 0, 0, 1, 0],
            [0, 0, 0, 0, 0, 1, 0, 1, 1, 0],
            [0, 0, 0, 1, 0, 0, 0, 0, 1, 1],
            [0, 0, 0, 0, 0, 0, 1, 1, 0, 0]
        ],
        layout: {
            title: "matrix",
            binary: true,
            binaryShape: "circle",
            color: "black",
            color2: "translucent",
            shape: "circle",
            stripe: false,
            highlight: false,
            background: "purple",
            background2: "translucent"
        }
    },

    top: {
        data: [{value: 1, highlight:true}, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        layout: {
            title: "matrix",
            binary: true,
            binaryShape: "circle",
            color: "black",
            color2: "translucent",
            shape: "circle",
            stripe: false,
            highlight: false,
            background: "translucent",
            background2: "translucent"
        }
    },

    right: {
        data: [
            {
                data: [1,2,3,4,5,6,7,8,9,10],
                layout: {
                    title: "Tool Hops",
                    binary: false,
                    binaryShape: "circle",
                    color: "#400FF2",
                    color2: "translucent",
                    shape: "circle",
                    stripe: false,
                    highlight: false,
                    background: "translucent",
                    background2: "translucent"
                }
            },
            {
                data:  [1,2,3,4,5,6,7,8,9,10],
                layout: {
                    title: "KTNS Hops",
                    binary: false,
                    binaryShape: "circle",
                    color: "#1F0092",
                    color2: "translucent",
                    shape: "circle",
                    stripe: false,
                    highlight: false,
                    background: "translucent",
                    background2: "translucent"
                }
            },
        ],

        title: "matrix",
        binary: true,
        color: "black",
        color2: "translucent",
        shape: "circle",
        stripe: false,
        background: "translucent",
        background2: "translucent",
    },

    bottom: {
        data: [
            {
                data: [1,2,3,4,5,6,7,8,9,10],
                layout: {
                    title: "Switches",
                    binary: true,
                    binaryShape: "circle",
                    color: "#0073FF",
                    color2: "translucent",
                    shape: "circle",
                    stripe: false,
                    highlight: false,
                    background: "translucent",
                    background2: "translucent"
                }
            },
            {
                data:  [1,2,3,4,5,6,7,8,9,10],
                layout: {
                    title: "T Distance",
                    binary: true,
                    binaryShape: "circle",
                    color: "#ff7700",
                    color2: "translucent",
                    shape: "circle",
                    stripe: false,
                    highlight: false,
                    background: "translucent",
                    background2: "translucent"
                }
            },
        ],

        title: "matrix",
        binary: true,
        color: "black",
        color2: "translucent",
        shape: "circle",
        stripe: false,
        background: "translucent",
        background2: "translucent",
    },


    left: {
        data: [1, {value: 1, highlight:true}, 3, 4, 5, 6, 7, 8, 9, 10],
        layout: {
            title: "matrix",
            binary: true,
            binaryShape: "circle",
            color: "black",
            color2: "translucent",
            shape: "circle",
            stripe: false,
            highlight: false,
            background: "translucent",
            background2: "translucent"
        }
    },

}






    [[{'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}], [{'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}], [{'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}], [{'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}], [{'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}], [{'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 1, 'highlight': False}], [{'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}], [{'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}], [{'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}], [{'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 1, 'highlight': False}], [{'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}], [{'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 1, 'highlight': False}], [{'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}], [{'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}], [{'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 1, 'highlight': False}, {'value': 0, 'highlight': False}, {'value': 0, 'highlight': False}]]