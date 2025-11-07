class FileUploader extends HTMLElement {
    constructor() {
    super(); // 상위 클래스의 생성자 호출
    const shadow = this.attachShadow({ mode: "closed" });
    this.shadow = shadow;
    //         this.innerHTML = `
    //     <div id="vueApp">{{"{{ message }}"}}</div>
    // `;
}

    connectedCallback() {
        const style = document.createElement("style");
        style.textContent = `
          div {
            width: 300px;
            display: flex;
            gap: 5px;
            flex-direction: column;
            align-items: flex-start;
            margin: 6px;
          }
          label {
            margin-right: 10px;
            font-size: 1.1rem;
          }
          input {
            border: 1px solid;
            border-radius: 4px;
            height: 18px;
            width: 100%;
          }
        `;
        this.shadow.appendChild(style);

        // const $label = document.createElement("label");
        // $label.innerText = `${this.name}을 입력하세요!`;
        // $label.setAttribute("for", this.id);
        //
        // const $input = document.createElement("input");
        // $input.setAttribute("type", "text");
        // $input.setAttribute("id", this.id);
        //
        // this.appendChild($label);
        // this.appendChild($input);
    }

    disconnectedCallback() {
        console.log("Custom element removed from page.");
    }

    connectedMoveCallback() {
        console.log("Custom element moved with moveBefore()");
    }

    adoptedCallback() {
        console.log("Custom element moved to new page.");
    }

    static get observedAttributes() {
        return ["input-name", "el-value", "placeholder"];
    }

    attributeChangedCallback(name, oldValue, newValue) {
        // if (this.querySelector("label")) {
        //     this.querySelector("label").innerText = `${this.name}을 입력하세요!`;
        // }
        console.log(`Attribute ${name} has changed.`);
        this._render();
    }

    get id() {
        return this.getAttribute("input-id");
    }

    get name() {
        return this.getAttribute("input-name");
    }

    get value() {
        return this.getAttribute("el-value");
    }

    _render() {
        if (this.shadow.querySelector("div")) {
            this.shadow.removeChild(this.shadow.querySelector("div"));
        }
        // if (this.shadow.querySelector("label")) {
        //     this.shadow.removeChild(this.shadow.querySelector("label"));
        // }
        // if (this.shadow.querySelector("input")) {
        //     this.shadow.removeChild(this.shadow.querySelector("input"));
        // }

        const $div = document.createElement("div");
        const $label = document.createElement("label");
        $label.innerText = `${this.name}을 입력하세요!`;
        $label.setAttribute("for", this.id);

        const $input = document.createElement("input");
        $input.setAttribute("type", "text");
        $input.setAttribute("id", this.id);
        $input.setAttribute("value", this.value);
        $input.setAttribute("placeholder", 'test');

        $div.appendChild($label);
        $div.appendChild($input);
        this.shadow.appendChild($div);
    }
}

$(function () {
    // customElements.define("file-uploader", FileUploader);

    // document.getElementById("btnTest").addEventListener("click", () => {
    //     document
    //     .getElementById("nameInput")
    //     .setAttribute("input-name", "변경테스트");
    //     document
    //     .getElementById("nameInput")
    //     .setAttribute("placeholder", "변경테스트");
    // });
})