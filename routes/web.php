<?php

// use App\Http\Controllers\CalculatorController;
// use Illuminate\Support\Facades\Route;

// Route::get('/', function () {
//     return view('casio');
// });

// Route::get('/', [CalculatorController::class, 'index']);
// Route::post('/calculate', [CalculatorController::class, 'calculate'])->name('calculate');
// Route::get('/history', [CalculatorController::class, 'history'])->name('history');
// Route::post('/clear-history', [CalculatorController::class, 'clearHistory'])->name('clear.history');
// Route::get('/export/pdf', [CalculatorController::class, 'exportPdf'])->name('export.pdf');


use App\Http\Controllers\CalculatorController;
use Illuminate\Support\Facades\Route;

Route::get('/', [CalculatorController::class, 'index']);
Route::post('/calculate', [CalculatorController::class, 'calculate'])->name('calculate');
Route::get('/history', [CalculatorController::class, 'history'])->name('history');
Route::post('/clear-history', [CalculatorController::class, 'clearHistory'])->name('clear.history');
Route::get('/export/pdf', [CalculatorController::class, 'exportPdf'])->name('export.pdf');
