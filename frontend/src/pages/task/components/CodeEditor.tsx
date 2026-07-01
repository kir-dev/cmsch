import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { CodeLanguage } from '@/util/views/task.view'
import { type Grammar, highlight, languages } from 'prismjs'
import 'prismjs/components/prism-c'
import 'prismjs/components/prism-cpp'
import 'prismjs/components/prism-csharp'
import 'prismjs/components/prism-java'
import 'prismjs/components/prism-javascript'
import 'prismjs/components/prism-kotlin'
import 'prismjs/components/prism-python'
import 'prismjs/components/prism-sql'
import 'prismjs/components/prism-typescript'
import 'prismjs/themes/prism-okaidia.css'
import { useState } from 'react'
import Editor from 'react-simple-code-editor'

interface CodeEditorProps {
  code: string
  setCode: React.Dispatch<React.SetStateAction<string>>
  readonly: boolean
}

const LANGUAGE_GRAMMAR_MAP = new Map<string, Grammar>([
  [CodeLanguage.C, languages.c],
  [CodeLanguage.CPP, languages.cpp],
  [CodeLanguage.CSHARP, languages.csharp],
  [CodeLanguage.JAVA, languages.java],
  [CodeLanguage.JAVASCRIPT, languages.javascript],
  [CodeLanguage.TYPESCRIPT, languages.typescript],
  [CodeLanguage.SQL, languages.sql],
  [CodeLanguage.KOTLIN, languages.kotlin],
  [CodeLanguage.PYTHON, languages.python]
])

const CodeEditor = ({ code, setCode, readonly }: CodeEditorProps) => {
  const [selectedLanguage, setSelectedLanguage] = useState<string>(CodeLanguage.C)
  return (
    <div className="flex flex-col mt-5">
      <div className="flex justify-end">
        <Select onValueChange={setSelectedLanguage} value={selectedLanguage}>
          <SelectTrigger className="w-40">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value={CodeLanguage.C}>C</SelectItem>
            <SelectItem value={CodeLanguage.CPP}>C++</SelectItem>
            <SelectItem value={CodeLanguage.CSHARP}>C#</SelectItem>
            <SelectItem value={CodeLanguage.JAVA}>JAVA</SelectItem>
            <SelectItem value={CodeLanguage.JAVASCRIPT}>JavaScript</SelectItem>
            <SelectItem value={CodeLanguage.TYPESCRIPT}>TypeScript</SelectItem>
            <SelectItem value={CodeLanguage.SQL}>SQL</SelectItem>
            <SelectItem value={CodeLanguage.KOTLIN}>Kotlin</SelectItem>
            <SelectItem value={CodeLanguage.PYTHON}>Python</SelectItem>
          </SelectContent>
        </Select>
      </div>

      <div className="my-5">
        <Editor
          value={code}
          onValueChange={(code) => setCode(code)}
          highlight={(code) => highlight(code, LANGUAGE_GRAMMAR_MAP.get(selectedLanguage)!, selectedLanguage)}
          padding={10}
          readOnly={readonly}
          style={{
            fontFamily: '"Fira code", "Fira Mono", monospace',
            fontSize: 14,
            borderRadius: '0.375rem',
            backgroundColor: '#272822',
            color: 'var(--foreground)'
          }}
        />
      </div>
    </div>
  )
}

export default CodeEditor
