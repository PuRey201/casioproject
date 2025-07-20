<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Casio Calculator</title>
  <script src="https://cdn.tailwindcss.com"></script>
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500&display=swap');
    body {
      font-family: 'Inter', sans-serif;
    }
    .btn:active {
      transform: scale(0.95);
      filter: brightness(0.95);
    }
    .action-btn:active {
      transform: scale(0.95);
    }
    .history-item:hover {
      background-color: #f9fafb;
    }
    .transition-transform {
      transition: transform 0.3s ease;
    }
  </style>
</head>
<body class="bg-white flex justify-center items-center min-h-screen">
  <div class="flex flex-col gap-5 w-[327px] p-5 bg-white rounded-2xl shadow-md">

    <!-- Calculator View -->
    <div id="calculator-view">
      <div id="display" class="bg-gray-100 rounded-xl p-4 text-right text-4xl font-medium text-black shadow-sm overflow-x-auto whitespace-nowrap mb-6">0</div>

      <div class="grid grid-cols-4 gap-3 mb-4 mt-2">
        <button onclick="handleAcBack(); return false;" id="ac-back-btn" class="btn bg-gray-300 text-black rounded-full h-16 w-16 text-2xl">⌫</button>
        <button onclick="clearEntry(); return false;" class="btn bg-gray-300 text-black rounded-full h-16 w-16 text-2xl">C</button>
        <button onclick="toggleSign(); return false;" class="btn bg-gray-300 text-black rounded-full h-16 w-16 text-2xl">+/-</button>
        <button onclick="setOperation('/'); return false;" class="btn bg-amber-500 text-white rounded-full h-16 w-16 text-2xl">÷</button>

        <button onclick="appendNumber('7'); return false;" class="btn bg-gray-200 text-black rounded-full h-16 w-16 text-2xl">7</button>
        <button onclick="appendNumber('8'); return false;" class="btn bg-gray-200 text-black rounded-full h-16 w-16 text-2xl">8</button>
        <button onclick="appendNumber('9'); return false;" class="btn bg-gray-200 text-black rounded-full h-16 w-16 text-2xl">9</button>
        <button onclick="setOperation('*'); return false;" class="btn bg-amber-500 text-white rounded-full h-16 w-16 text-2xl">×</button>

        <button onclick="appendNumber('4'); return false;" class="btn bg-gray-200 text-black rounded-full h-16 w-16 text-2xl">4</button>
        <button onclick="appendNumber('5'); return false;" class="btn bg-gray-200 text-black rounded-full h-16 w-16 text-2xl">5</button>
        <button onclick="appendNumber('6'); return false;" class="btn bg-gray-200 text-black rounded-full h-16 w-16 text-2xl">6</button>
        <button onclick="setOperation('-'); return false;" class="btn bg-amber-500 text-white rounded-full h-16 w-16 text-2xl">−</button>

        <button onclick="appendNumber('1'); return false;" class="btn bg-gray-200 text-black rounded-full h-16 w-16 text-2xl">1</button>
        <button onclick="appendNumber('2'); return false;" class="btn bg-gray-200 text-black rounded-full h-16 w-16 text-2xl">2</button>
        <button onclick="appendNumber('3'); return false;" class="btn bg-gray-200 text-black rounded-full h-16 w-16 text-2xl">3</button>
        <button onclick="setOperation('+'); return false;" class="btn bg-amber-500 text-white rounded-full h-16 w-16 text-2xl">+</button>

        <button onclick="appendNumber('0'); return false;" class="btn bg-gray-200 text-black rounded-full h-16 col-span-2 text-2xl pl-7 text-left">0</button>
        <button onclick="appendNumber('.'); return false;" class="btn bg-gray-200 text-black rounded-full h-16 w-16 text-2xl">.</button>
        <button onclick="calculate(); return false;" class="btn bg-amber-500 text-white rounded-full h-16 w-full text-2xl col-span-4">=</button>
      </div>

      <div class="flex justify-between mt-2">
        <button onclick="toggleHistory(true); return false;" class="action-btn bg-gray-300 text-black rounded-full h-12 flex-1 mx-1">History</button>
        <button onclick="printInvoice(); return false;" class="action-btn bg-amber-500 text-white rounded-full h-12 flex-1 mx-1">Print</button>
      </div>
    </div>

    <!-- History View -->
    <div id="history-view" class="hidden flex flex-col gap-2 border-t pt-4 mt-4">
      <div class="flex justify-between items-center mb-2">
        <h2 class="text-lg font-semibold">History</h2>
        <div class="flex gap-2">
          <button onclick="showClearConfirmation(); return false;" class="text-sm text-red-500">Clear All</button>
          <button onclick="toggleHistory(false); return false;" class="text-sm text-blue-500">Back</button>
        </div>
      </div>

      <!-- Confirmation Modal -->
      <div id="confirm-modal" class="fixed inset-0 bg-black bg-opacity-30 flex items-center justify-center z-50 hidden">
        <div class="bg-white rounded-xl p-6 w-[90%] max-w-sm shadow-lg">
          <h2 class="text-lg font-semibold text-center mb-4">Clear All History?</h2>
          <p class="text-sm text-gray-600 text-center mb-6">This action cannot be undone.</p>
          <div class="flex justify-center gap-4">
            <button onclick="clearHistory(); return false;" class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg text-sm">
              Yes, Clear
            </button>
            <button onclick="closeModal(); return false;" class="bg-gray-300 hover:bg-gray-400 text-black px-4 py-2 rounded-lg text-sm">
              Cancel
            </button>
          </div>
        </div>
      </div>

      <div id="history-list" class="bg-gray-50 rounded-lg p-3 shadow-inner max-h-60 overflow-y-auto border border-gray-200">
        <!-- Loading spinner will appear here temporarily -->
      </div>
    </div>

  </div>

  <script>
    let display = document.getElementById('display');
    let currentInput = '';
    let previousInput = '';
    let operation = null;
    let shouldResetInput = false;
    let calculationHistory = [];

    function updateDisplay() {
      if (operation !== null && !shouldResetInput) {
        display.textContent = `${previousInput}${getSymbol(operation)}${currentInput}`;
      } else if (operation !== null && shouldResetInput) {
        display.textContent = `${previousInput}${getSymbol(operation)}`;
      } else {
        display.textContent = currentInput || '0';
      }
      updateAcBackButton();
    }

    function showClearConfirmation() {
      document.getElementById('confirm-modal').classList.remove('hidden');
    }

    function closeModal() {
      document.getElementById('confirm-modal').classList.add('hidden');
    }

    function clearHistory() {
      calculationHistory = [];
      updateHistoryDisplay();
      closeModal();
    }

    function clearEntry() {
      currentInput = '';
      updateDisplay();
    }

    function handleAcBack() {
      if (currentInput) {
        currentInput = currentInput.slice(0, -1);
      } else {
        clearAll();
      }
      updateDisplay();
    }

    function updateAcBackButton() {
      const btn = document.getElementById('ac-back-btn');
      btn.textContent = currentInput ? '⌫' : 'AC';
    }

    function appendNumber(number) {
      if (shouldResetInput) {
        currentInput = '';
        shouldResetInput = false;
      }
      if (number === '.' && currentInput.includes('.')) return;
      currentInput += number;
      updateDisplay();
    }

    function clearAll() {
      currentInput = '';
      previousInput = '';
      operation = null;
      updateDisplay();
    }

    function toggleSign() {
      if (!currentInput) return;
      currentInput = (parseFloat(currentInput) * -1).toString();
      updateDisplay();
    }

    function setOperation(op) {
      if (currentInput === '' && previousInput === '') return;
      if (previousInput !== '' && currentInput !== '') {
        calculate();
      }
      operation = op;
      if (currentInput !== '') {
        previousInput = currentInput;
        currentInput = '';
      }
      shouldResetInput = false;
      updateDisplay();
    }

    function calculate() {
      if (operation === null || currentInput === '' || previousInput === '') return;
      const prev = parseFloat(previousInput);
      const current = parseFloat(currentInput);
      let result;

      switch (operation) {
        case '+': result = prev + current; break;
        case '-': result = prev - current; break;
        case '*': result = prev * current; break;
        case '/': result = prev / current; break;
        default: return;
      }

      const expression = `${previousInput} ${getSymbol(operation)} ${currentInput}`;
      addToHistory(expression, result.toString());

      currentInput = result.toString();
      previousInput = '';
      operation = null;
      shouldResetInput = true;
      updateDisplay();
    }

    function getSymbol(op) {
      return op === '*' ? '×' : op === '/' ? '÷' : op;
    }

    function addToHistory(expression, result) {
      const now = new Date();
      const time = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
      calculationHistory.unshift({ expression, result, time });
      updateHistoryDisplay();
    }

    function updateHistoryDisplay() {
      const list = document.getElementById('history-list');
      list.innerHTML = '';

      calculationHistory.forEach((item, index) => {
        const wrapper = document.createElement('div');
        wrapper.className = 'relative overflow-hidden rounded mb-2 h-16';
        wrapper.style.touchAction = 'pan-y'; // prevent scroll interference

        // Red background with trash icon
        const bg = document.createElement('div');
        bg.className = 'absolute inset-0 bg-red-500 flex items-center justify-end pr-4';
        bg.innerHTML = '🗑️';
        bg.style.zIndex = '0';

        // Foreground swipeable container
        const swipeContainer = document.createElement('div');
        swipeContainer.className = 'absolute inset-0 bg-white p-3 flex justify-between items-center transition-transform duration-300 ease-in-out';
        swipeContainer.style.zIndex = '10';
        swipeContainer.style.transform = 'translateX(0)';

        swipeContainer.innerHTML = `
          <div class="text-gray-800">${item.expression} <span class="font-bold text-amber-500">= ${item.result}</span></div>
          <div class="text-gray-400 text-sm">${item.time}</div>
        `;

        // Append background and swipe content
        wrapper.appendChild(bg);
        wrapper.appendChild(swipeContainer);
        list.appendChild(wrapper);

        // --- Swipe logic ---
        let startX = 0;
        let currentX = 0;
        let isDragging = false;

        const onDragStart = (x) => {
          startX = x;
          isDragging = true;
          swipeContainer.style.transition = 'none';
        };

        const onDragMove = (x) => {
          if (!isDragging) return;
          currentX = x;
          const delta = currentX - startX;
          if (delta < 0) {
            swipeContainer.style.transform = `translateX(${delta}px)`;
          }
        };

        const onDragEnd = () => {
          isDragging = false;
          swipeContainer.style.transition = 'transform 0.3s ease';
          const delta = currentX - startX;

          if (delta < -80) {
            // Animate out
            swipeContainer.style.transform = 'translateX(-100%)';
            swipeContainer.style.opacity = '0';
            setTimeout(() => {
              calculationHistory.splice(index, 1);
              updateHistoryDisplay();
            }, 200);
          } else {
            // Return to normal
            swipeContainer.style.transform = 'translateX(0)';
          }
        };

        // Mobile
        swipeContainer.addEventListener('touchstart', e => onDragStart(e.touches[0].clientX));
        swipeContainer.addEventListener('touchmove', e => onDragMove(e.touches[0].clientX));
        swipeContainer.addEventListener('touchend', onDragEnd);

        // Desktop
        swipeContainer.addEventListener('mousedown', e => onDragStart(e.clientX));
        swipeContainer.addEventListener('mousemove', e => {
          if (isDragging) onDragMove(e.clientX);
        });
        swipeContainer.addEventListener('mouseup', onDragEnd);
        swipeContainer.addEventListener('mouseleave', () => {
          if (isDragging) onDragEnd();
        });
      });

      if (calculationHistory.length === 0) {
        list.innerHTML = '<div class="text-center text-gray-400 py-4">No calculations yet</div>';
      }
    }

    function toggleHistory(show) {
      const historyView = document.getElementById('history-view');
      const historyList = document.getElementById('history-list');
      
      if (show) {
        historyView.classList.remove('hidden');
        
        // Show loading spinner
        historyList.innerHTML = `
          <div class="flex flex-col items-center justify-center py-4 text-gray-500 text-sm">
            <div class="w-6 h-6 border-4 border-gray-300 border-t-blue-500 rounded-full animate-spin mb-2"></div>
            Loading history...
          </div>
        `;
        
        // Simulate loading delay (700ms) then show actual history
        setTimeout(() => {
          updateHistoryDisplay();
        }, 700);
      } else {
        historyView.classList.add('hidden');
      }
    }

    function printInvoice() {
      const now = new Date();
      const dateStr = now.toLocaleDateString();
      const timeStr = now.toLocaleTimeString();
      const total = calculationHistory.reduce((sum, item) => sum + parseFloat(item.result), 0);
      const totalCalcs = calculationHistory.length;

      const rows = calculationHistory.map(item => `
        <div class="record" style="
          background-color: #f3f4f6;
          border-radius: 8px;
          padding: 10px 14px;
          margin-bottom: 10px;
        ">
          <div class="line" style="display: flex; justify-content: space-between; align-items: center;">
            <div>
              <div><strong>${item.expression}</strong></div>
              <div>= <strong>${item.result}</strong></div>
            </div>
            <div class="timestamp" style="font-size: 10px; color: gray; white-space: nowrap; padding-left: 10px; min-width: 60px; text-align: right;">
              ${item.time}
            </div>
          </div>
        </div>
      `).join('');

      const printWindow = window.open('', '', 'width=800,height=600');
      printWindow.document.write(`
        <html>
          <head>
            <title>Casio Calculator History</title>
            <style>
              body {
                font-family: Arial, sans-serif;
                padding: 20px;
                margin: 0;
                color: #000;
              }
              h1 {
                text-align: center;
                font-size: 20px;
                margin-bottom: 5px;
              }
              .info {
                text-align: center;
                font-size: 12px;
                color: #555;
                margin-bottom: 10px;
              }
              .total {
                font-size: 16px;
                font-weight: bold;
                color: blue;
                margin-bottom: 10px;
                text-align: center;
              }
              .record {
                margin-bottom: 6px;
                line-height: 1.2;
              }
              .line {
                display: flex;
                justify-content: space-between;
                align-items: center;
              }
              .timestamp {
                font-size: 10px;
                color: gray;
                white-space: nowrap;
                padding-left: 10px;
                min-width: 60px;
                text-align: right;
              }
              .total-calcs {
                text-align: center;
                font-size: 14px;
                font-weight: bold;
                margin-top: 15px;
              }
            </style>
          </head>
          <body>
            <h1>CASIO Calculator</h1>
            <div class="info">Printed on ${dateStr} at ${timeStr}</div>
            <div class="total">Grand Total = ${total}</div>
            ${rows}
            <hr style="margin: 15px 0;">
            <div class="total-calcs">Total Calculations: ${totalCalcs}</div>
          </body>
        </html>
      `);
      printWindow.document.close();
      printWindow.focus();
      printWindow.print();
    }

    updateDisplay();
  </script>
</body>
</html>