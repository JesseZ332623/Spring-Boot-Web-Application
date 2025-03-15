/**
 * 通用响应处理，在网页的指定位置插入响应体。
 * 
 * @param {*} data          JSON 响应体
 * @param {*} elementId     要插入的标签 id
 */
function handleResponse(data, elementId) 
{
    // 通过传入的元素 id 获取这个 DOM
    const resultDiv = document.getElementById(elementId);

    // 检查 JSON 响应体的 ifSuccess 字段
    if (data.ifSuccess) 
    {
        const tableHtml = generateTableFromData(data);

        // 查询成功的话就插入数据字符串
        resultDiv.innerHTML = `
            <div style = "color: green;">
                ✅ ${data.infoMessage} <br>
                ${tableHtml}
            </div>`;
    }
    else 
    {
        // 否则插入失败的字符串
        resultDiv.innerHTML = `
            <div style="color: red;">
                ❌ ${data.errorMessage} Status: [${data.status}]
            </div>`;
    }
}

/**
 * 将 JSON 数据转换为表格 HTML
 * 
 * @param   {Object} data   需要渲染的 JSON 数据对象
 * @returns {string}        表格的 HTML 字符串
 */
function generateTableFromData(data) 
{
    // 检查数据是否为对象类型
    if (typeof data !== 'object' || data === null) {
        return `<div>数据格式不支持表格渲染</div>`;
    }

    // 生成表格结构
    let table = '<table border="1" style="border-collapse: collapse; width: 65%; margin-top: 10px;">';
    
    // 添加表头（可选）
    table += `
        <thead>
            <tr style="background-color: #f0f0f0; font-weight: bold;">
                <th style="padding: 3px; border: 1px solid #ddd;">Key</th>
                <th style="padding: 3px; border: 1px solid #ddd;">Value</th>
            </tr>
        </thead>
    `;

    // 遍历数据生成行
    table += '<tbody>';
    for (const [key, value] of Object.entries(data.data)) {
        table += `
            <tr>
                <td style="padding: 8px; border: 1px solid #ddd;">${key}</td>
                <td style="padding: 8px; border: 1px solid #ddd;">${value}</td>
            </tr>
        `;
    }

    table += '</tbody></table>';

    return table;
}


/**
 * 格式化表格单元格的值（处理嵌套对象/数组）。
 * 
 * @param {*} value     单元格的原始值
 * 
 * @returns {string}    格式化后的字符串
 */
function formatValue(value)
{
    if (typeof value === 'object' && value !== null)
    {
        if (Array.isArray(value)) {
            return `[数组] ${value.map((val) => { JSON.stringify(val) }).join(', ')}`;
        }
        else {
            return `[对象] ${JSON.stringify(value, null, 2)}`;
        }

        return String(value);
    }
}

/**
 * 验证 JSON 体 country 内部数据的合法性。
 * 
 * @param {*} country 从 input 输入框中整合的数据，JSON 格式
*/
function verifyInput(country) 
{
    let isValid = true;

    // 使用 entries() 转化成数组
    const countryEntries = Object.entries(country);

    // 使用 for ... of 循环按照 [key, value] 这个 pair 的形式遍历数组。
    for (const [key, value] of countryEntries) 
    {
        /*
            动态的拼接每一个 input 输入框的 id，
            交给 getElementById() 获取 DOM。

            其中：slice(begin, end) 获取调用该方法的字符串 [begin, end] 范围内的子串，
            如果 end 没填，则默认到字符串末尾。
        */
        const inputElement = document.getElementById(
            'new' + key.charAt(0).toUpperCase() + key.slice(1)
        );
        
        // 如果有任何一个输入框的值为空字符，弹窗并让焦点回到这个输入框。
        if (value === '')
        {
            alert(`${key} can not be empty.`);
            inputElement.focus();
            isValid = false;

            break;  // 跳出循环
        }
    }

    return isValid;
}

/**
 * 对于 id 为 jumpButton 的按钮执行的操作。
*/
function getAllCountries() 
{
    /*
        获取 id 为 jumpButton 的按钮的 DOM，为点击事件添加新的监听：
        直接让 window.location.href 的值为相对 URL /country/CountriesList，
        这回自行调用 Get 方法，触发浏览器的视图渲染，跳转到新页面。
    */
    document.getElementById('jumpButton').addEventListener(
        'click',
        function () {
            window.location.href = `/country/CountriesList`;
        }
    );
}

/**
 * 查询国家信息。
*/
function getCountry() 
{
    /*
        调用 getElementById()，
        从 id 为 countryCode 中的 DOM 获取对象。
    */
    const codeInput = document.getElementById('countryCode');

    // 获取对象值（这里是字符串），再去除空格。
    const code = codeInput.value.trim();

    if (code === '')    // 检查用户输入是否为空
    {
        alert("Please enter Country Code!");
        codeInput.focus();  // 设置浏览器焦点回到这个表单

        return;
    }

    /*
        对 URL /country/${code} 发起异步（async）的 Get 请求，
        其中 ${code} 表示取出字符串 code 的值（JS 术语是字符串模板）。

        可能的 URL 为：https://localhost:8080/country/CHN

        fetch() 操作返回一个 Promise<Response> 类型，
        Promise<> 有三种状态：

        1. 待定（Pending）      初始态，没有兑现也没有拒绝。
        2. 已兑现（Fulfilled）  意味着操作完成。
        3. 已拒绝（Rejected）   意味着操作失败。

        Promise<> 内部的 then()，catch() 方法支持链式调用（因为它们同样返回 Promise<>）。
        
        如果请求执行成功（已兑现），会先跳转至第一个 then() 方法，
        把响应体（response） JSON 化，然后返回一个 Promise<>。
        然后调用上一个 then() 返回的 Promise<> 的 then() 方法，
        调用 handleResponse() 传递数据，和标签 id，在页面上执行渲染。

        如果请求失败（响应码非 200），则直接跳转至 catch()，
        捕获 fetch() 执行过程中的错误，写入错误日志。
    */
    fetch(`/country/${code}`)
        .then(response =>  response.json())
        .then(data => handleResponse(data, 'getResult'))
        .catch(error => console.error('Error:', error));
}

/**
 * 添加新国家数据行。
*/
function addCountry() 
{
    // 获取所有输入框的值组成对象（JavaScript 中的对象是由键值对组成的）。
    const country = {
        code:               document.getElementById('newCode').value,
        name:               document.getElementById('newName').value,
        continent:          document.getElementById('newContinent').value,
        region:             document.getElementById('newRegion').value,
        surfaceArea:        document.getElementById('newSurfaceArea').value,
        indepYear:          document.getElementById('newIndepYear').value,
        population:         document.getElementById('newPopulation').value,
        lifeExpectancy:     document.getElementById('newLifeExpectancy').value,
        gnp:                document.getElementById('newGNP').value,
        gnpOld:             document.getElementById('newGNPOld').value,
        localName:          document.getElementById('newLocalName').value,
        governmentForm:     document.getElementById('newGovernmentForm').value,
        headOfState:        document.getElementById('newHeadOfState').value,
        capital:            document.getElementById('newCapital').value,
        code2:              document.getElementById('newCode2').value
    };

    // 验证对象体的合法性（不能有空）
    if (!verifyInput(country)) { return; }

    /*
        对 URL /country 发起 POST 请求（非 GET 请求需要自行指定请求方法）。
        请求头说明请求体内容类型为 JSON
        请求体为 country 对象的 JSON 的文本形式。
    */
    fetch('/country', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(country)
    })
        .then(response => response.json())
        .then(data => handleResponse(data, 'postResult'))
        .catch(error => console.error('Error:', error));
}

/** 
  * 更新国家信息。
*/
function updateCountry() 
{
    const country = {
        code:               document.getElementById('updateCode').value,
        name:               document.getElementById('updateName').value,
        continent:          document.getElementById('updateContinent').value,
        region:             document.getElementById('updateRegion').value,
        surfaceArea:        document.getElementById('updateSurfaceArea').value,
        indepYear:          document.getElementById('updateIndepYear').value,
        population:         document.getElementById('updatePopulation').value,
        lifeExpectancy:     document.getElementById('updateLifeExpectancy').value,
        gnp:                document.getElementById('updateGNP').value,
        gnpOld:             document.getElementById('updateGNPOld').value,
        localName:          document.getElementById('updateLocalName').value,
        governmentForm:     document.getElementById('updateGovernmentForm').value,
        headOfState:        document.getElementById('updateHeadOfState').value,
        capital:            document.getElementById('updateCapital').value,
        code2:              document.getElementById('updateCode2').value
    };

    fetch('/country', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(country)
    })
        .then(response => response.json())
        .then(data => handleResponse(data, 'putResult'))
        .catch(error => console.error('Error:', error));
}

/**
 * 删除国家信息。
*/
function deleteCountry() 
{
    const codeInput = document.getElementById('deleteCode');
    const code = codeInput.value.trim();

    if (code === '') {
        alert("Please enter Country Code!");
        codeInput.focus();  // 设置浏览器焦点回到这个表单

        return;
    }

    fetch(`/country/${code}`, { method: 'DELETE' })
        .then(response => response.json())
        .then(data => handleResponse(data, 'deleteResult'))
        .catch(error => console.error('Error:', error));
}