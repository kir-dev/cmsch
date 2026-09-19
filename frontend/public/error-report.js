// Client error reporting: based on Sentry (https://github.com/getsentry/sentry-javascript),
const originalConsoleError = console.error
let previousReportSignature = null

function reportError(message, stack, source) {
  const signature = (message || '') + '\n' + (stack || '')
  if (previousReportSignature === signature) {
    return
  }
  previousReportSignature = signature

  const report = {
    message: message,
    stack: stack,
    source: source || 'window',
    userAgent: navigator.userAgent,
    href: window.location.href
  }

  const apiBase = window.config.API_BASE_URL || 'http://localhost:8080'
  fetch(apiBase + '/api/error/submit', {
    body: JSON.stringify(report),
    method: 'POST',
    credentials: 'include',
    headers: { 'Content-Type': 'application/json' }
  }).catch((err) => originalConsoleError('Failed to report error', err))
}

const NOISE_PATTERNS = [
  /^Script error\.?$/,
  /^Javascript error: Script error\.? on line 0$/,
  /^ResizeObserver loop completed with undelivered notifications\.$/,
  /^Cannot redefine property: googletag$/,
  /^Can't find variable: gmo$/,
  /^undefined is not an object \(evaluating 'a\.[A-Z]'\)$/,
  /can't redefine non-configurable property "solana"/,
  /vv\(\)\.getRestrictions is not a function/,
  /Can't find variable: _AutofillCallbackHandler/,
  /Object Not Found Matching Id:\d+, MethodName:simulateEvent/,
  /Java exception was raised during method invocation$/,
  /Java object is gone$/
]

function isNoise(message, stack) {
  const text = (message || '') + '\n' + (stack || '')
  if (
    text.includes('iabjs://') ||
    text.includes('webkit-masked-url://') ||
    text.includes('__gCrWeb') ||
    text.includes('__firefox__') ||
    text.includes('DarkReader') ||
    text.includes('window.ethereum') ||
    text.includes('contentScriptData') ||
    text.includes('runtime.sendMessage()') ||
    text.includes('standardSelectors') ||
    text.includes('translateDisabled') ||
    text.includes('chrome-extension://') ||
    text.includes('import.meta.resolve not supported') ||
    text.includes('og:type')
  ) {
    return true
  }
  return NOISE_PATTERNS.some((pattern) => pattern.test(message || '') || pattern.test(text))
}

function processAndReportError(error, source) {
  if (!error || typeof error !== 'object') {
    const message = typeof error === 'string' ? error : JSON.stringify(error)
    if (!isNoise(message, null)) {
      reportError(message, null, source)
    }
  } else {
    const message = error.message || null
    const stack = error.stack || null
    if (!isNoise(message, stack)) {
      reportError(message, stack, source)
    }
  }
}

window.addEventListener('unhandledrejection', (error) => processAndReportError(error.reason, 'unhandledrejection'))
window.addEventListener('error', (error) => processAndReportError(error.error || error.message, 'error'))

;(function () {
  try {
    if (typeof console.error !== 'function' || console.error.__cmschOriginal) {
      return
    }

    function isErrorLike(arg) {
      return arg instanceof Error || (arg && typeof arg === 'object' && Object.prototype.toString.call(arg) === '[object Error]')
    }

    function stringifyArg(arg) {
      if (typeof arg === 'string') return arg
      try {
        return JSON.stringify(arg)
      } catch (e) {
        return String(arg)
      }
    }

    const original = console.error
    const wrapped = function (...args) {
      try {
        const errorArg = args.find(isErrorLike)
        if (errorArg) {
          processAndReportError(errorArg, 'console')
        } else if (args.length > 0) {
          const message = args.map(stringifyArg).join(' ').slice(0, 2000)
          if (!isNoise(message, null)) {
            reportError(message, null, 'console')
          }
        }
      } catch (e) {}
      original.apply(console, args)
    }
    wrapped.__cmschOriginal = original
    console.error = wrapped
  } catch (e) {}
})()
