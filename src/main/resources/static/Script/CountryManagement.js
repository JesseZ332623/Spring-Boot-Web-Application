    // 通用响应处理
    function handleResponse(data, elementId)
    {
        const resultDiv = document.getElementById(elementId);

        if (data.ifSuccess)
        {
            resultDiv.innerHTML = `
            <div style = "color: green;">
                ✅ ${data.infoMessage} <br>
                <pre>${JSON.stringify(data.data, null, 2)}</pre>
            </div>`;
        }
        else
        {
            resultDiv.innerHTML = `
            <div style="color: red;">
                ❌ ${data.errorMessage}<br>
                    Status: ${data.status}
            </div>`;
        }
    }

    function getAllCountries()
    {
        document.getElementById('jumpButton').addEventListener(
            'click',
            function() { window.location.href = `/country/CountriesList`; }
        );
    }

    // 获取国家信息
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
            对 URL /country/${code} 发起异步的 Get 请求，
            其中 ${code} 表示取出字符串 code 的值。
            可能的 URL 为：https://localhost:8080/country/CHN
        */
        fetch(`/country/${code}`)
            .then(response => response.json())
            .then(data => handleResponse(data, 'getResult'))
            .catch(error => console.error('Error:', error));
    }

    // 添加新国家
    function addCountry()
    {
        const country = {
            code: document.getElementById('newCode').value,
            name: document.getElementById('newName').value,
            continent: document.getElementById('newContinent').value,
            region: document.getElementById('newRegion').value,
            surfaceArea: document.getElementById('newSurfaceArea').value,
            indepYear: document.getElementById('newIndepYear').value,
            population: document.getElementById('newPopulation').value,
            lifeExpectancy: document.getElementById('newLifeExpectancy').value,
            gnp: document.getElementById('newGNP').value,
            gnpOld: document.getElementById('newGNPOld').value,
            localName: document.getElementById('newLocalName').value,
            governmentForm: document.getElementById('newGovernmentForm').value,
            headOfState: document.getElementById('newHeadOfState').value,
            capital: document.getElementById('newCapital').value,
            code2: document.getElementById('newCode2').value
        };

        fetch('/country', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(country)
        })
        .then(response => response.json())
        .then(data => handleResponse(data, 'postResult'))
        .catch(error => console.error('Error:', error));
    }

    // 更新国家信息
    function updateCountry()
    {
        const country = {
            code: document.getElementById('updateCode').value,
            name: document.getElementById('updateName').value,
            continent: document.getElementById('updateContinent').value,
            region: document.getElementById('updateRegion').value,
            surfaceArea: document.getElementById('updateSurfaceArea').value,
            indepYear: document.getElementById('updateIndepYear').value,
            population: document.getElementById('updatePopulation').value,
            lifeExpectancy: document.getElementById('updateLifeExpectancy').value,
            gnp: document.getElementById('updateGNP').value,
            gnpOld: document.getElementById('updateGNPOld').value,
            localName: document.getElementById('updateLocalName').value,
            governmentForm: document.getElementById('updateGovernmentForm').value,
            headOfState: document.getElementById('updateHeadOfState').value,
            capital: document.getElementById('updateCapital').value,
            code2: document.getElementById('updateCode2').value
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

    // 删除国家
    function deleteCountry()
    {
        const codeInput = document.getElementById('deleteCode');
        const code      = codeInput.value.trim();

        if (code === '') {
            alert("Please enter Country Code!");
            codeInput.focus();  // 设置浏览器焦点回到这个表单

            return;
        }

        fetch(`/country/${code}`, { method: 'DELETE'})
        .then(response => response.json())
        .then(data => handleResponse(data, 'deleteResult'))
        .catch(error => console.error('Error:', error));
    }