import { useEffect, useState } from 'react';
import { apiList, apiCreate, apiUpdate, apiDelete } from '../api';

export type Column<T> = {
  key: Extract<keyof T, string>;
  label: string;
  type?: 'string' | 'number' | 'bigdecimal';
};

type Props<T> = {
  resource: string;
  columns: Column<T>[];
  title?: string;
  idField?: Extract<keyof T, string>;
};

type CreateMarker = { __isCreating: true };

export default function CrudPage<T extends Record<string, unknown> = Record<string, unknown>>({
  resource,
  columns,
  title,
  idField,
}: Props<T>) {
  const defaultId = ('id' as unknown) as Extract<keyof T, string>;
  const idKey = (idField ?? defaultId) as Extract<keyof T, string>;

  const [items, setItems] = useState<T[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState<number>(1);
  const pageSize = 10;

  const [editing, setEditing] = useState<T | CreateMarker | null>(null);
  const initialFormState = columns.reduce((acc, col) => {
    acc[col.key] = undefined;
    return acc;
  }, {} as Partial<T>);
  const [form, setForm] = useState<Partial<T>>(initialFormState);

  async function load(): Promise<void> {
    setLoading(true);
    setError(null);
    try {
      const data = (await apiList<T>(resource)) ?? [];
      setItems(Array.isArray(data) ? data : []);
    } catch (e) {
      setError((e as Error)?.message ?? String(e));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [resource]);

  function startCreate(): void {
    setForm(initialFormState);
    setEditing({ __isCreating: true } as CreateMarker);
  }
  function startEdit(it: T): void {
    setForm({ ...(it as Record<string, unknown>) } as Partial<T>);
    setEditing(it);
  }
  function cancelEdit(): void {
    setEditing(null);
    setForm({});
  }

  async function save(): Promise<void> {
    try {
      const isCreate = editing && (editing as CreateMarker).__isCreating;
      const idValue = !isCreate ? (editing as T)[idKey as keyof T] : undefined;
      if (idValue !== undefined && idValue !== null) {
        await apiUpdate<T, Partial<T>>(resource, String(idValue), form);
      } else {
        await apiCreate<T, Partial<T>>(resource, form);
      }
      await load();
      cancelEdit();
    } catch (e) {
      setError((e as Error)?.message ?? String(e));
    }
  }

  async function remove(it: T): Promise<void> {
    const idValue = (it as Record<string, unknown>)[idKey] as number | string | undefined;
    if (!confirm(`Delete ${resource} ${String(idValue)}?`)) return;
    try {
      if (idValue !== undefined) {
        await apiDelete(resource, String(idValue));
        await load();
      } else {
        setError('Unable to delete: missing id');
      }
    } catch (e) {
      setError((e as Error)?.message ?? String(e));
    }
  }

  const start = (page - 1) * pageSize;
  const pageItems = items.slice(start, start + pageSize);
  const totalPages = Math.max(1, Math.ceil(items.length / pageSize));

  return (
    <div style={{ padding: 12 }}>
      <h2>{title || resource}</h2>
      <div style={{ marginBottom: 8 }}>
        <button onClick={startCreate}>Create</button>
        <button onClick={load} style={{ marginLeft: 8 }}>
          Refresh
        </button>
      </div>

      {loading && <div>Loading...</div>}
      {error && <div style={{ color: 'red' }}>{error}</div>}

      <table border={1} cellPadding={6} style={{ borderCollapse: 'collapse', width: '100%' }}>
        <thead>
          <tr>
            {columns.map((c) => (
              <th key={c.key}>{c.label}</th>
            ))}
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {pageItems.map((it) => {
            const keyValue = (it as Record<string, unknown>)[idKey];
            const rowKey = keyValue !== undefined ? String(keyValue) : JSON.stringify(it);
            return (
              <tr key={rowKey}>
                {columns.map((c) => {
                  const cell = (it as Record<string, unknown>)[c.key];
                  return <td key={c.key}>{cell === undefined || cell === null ? '' : String(cell)}</td>;
                })}
                <td>
                  <button onClick={() => startEdit(it)}>Edit</button>
                  <button onClick={() => remove(it)} style={{ marginLeft: 6 }}>
                    Delete
                  </button>
                </td>
              </tr>
            );
          })}
          {pageItems.length === 0 && (
            <tr>
              <td colSpan={columns.length + 1}>No data</td>
            </tr>
          )}
        </tbody>
      </table>

      <div style={{ marginTop: 8 }}>
        <button onClick={() => setPage((p) => Math.max(1, p - 1))} disabled={page === 1}>
          Prev
        </button>
        <span style={{ margin: '0 8px' }}>
          Page {page} / {totalPages}
        </span>
        <button onClick={() => setPage((p) => Math.min(totalPages, p + 1))} disabled={page === totalPages}>
          Next
        </button>
      </div>

      {editing !== null && (
        <div style={{ marginTop: 12, padding: 8, border: '1px solid #ccc' }}>
          <h3>{(editing as Record<string, unknown>)[idKey] ? 'Edit' : 'Create'}</h3>
          <div style={{ display: 'grid', gap: 8 }}>
            {columns
              .filter((c) => c.key !== idKey)
              .map((col) => {
                const key = col.key;
                const value = (form as Record<string, unknown>)[key];
                return (
                  <label key={key}>
                    <div style={{ fontSize: 12 }}>{col.label}</div>
                    <input
                      value={value === undefined || value === null ? '' : String(value)}
                      onChange={(e) =>
                        setForm((prev) => {
                          const next = { ...(prev as Record<string, unknown>) };
                          // store as string — backend conversion expected
                          next[key] = e.target.value;
                          return next as Partial<T>;
                        })
                      }
                      style={{ width: '100%' }}
                    />
                  </label>
                );
              })}
          </div>
          <div style={{ marginTop: 8 }}>
            <button onClick={save}>Save</button>
            <button onClick={cancelEdit} style={{ marginLeft: 8 }}>
              Cancel
            </button>
          </div>
        </div>
      )}
    </div>
  );
}