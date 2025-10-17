import React, { useEffect, useState } from 'react';
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

/**
 * Generic CRUD page. T is the item shape (use `CrudPage<MyType>` where convenient).
 */
export default function CrudPage<T extends Record<string, any> = any>({
  resource,
  columns,
  title,
  idField,
}: Props<T>) {
  const defaultId = ('id' as unknown) as Extract<keyof T, string>;
  const idKey = (idField ?? defaultId) as Extract<keyof T, string>;

  const [items, setItems] = useState<T[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [page, setPage] = useState(1);
  const pageSize = 10;

  const [editing, setEditing] = useState<T | CreateMarker | null>(null);
  const [form, setForm] = useState<Partial<T>>({});

  async function load() {
    setLoading(true);
    setError(null);
    try {
      const data = (await apiList<T>(resource)) ?? [];
      setItems(Array.isArray(data) ? data : []);
    } catch (e: any) {
      setError(e?.message ?? String(e));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [resource]);

  function startCreate() {
    setForm({});
    setEditing({ __isCreating: true } as CreateMarker);
  }
  function startEdit(it: T) {
    setForm({ ...it });
    setEditing(it);
  }
  function cancelEdit() {
    setEditing(null);
    setForm({});
  }

  async function save() {
    try {
      const isCreate = editing && (editing as CreateMarker).__isCreating;
      const idValue = !isCreate ? (editing as any)[idKey as string] : undefined;
      if (idValue !== undefined && idValue !== null) {
        await apiUpdate<T, Partial<T>>(resource, idValue, form);
      } else {
        await apiCreate<T, Partial<T>>(resource, form);
      }
      await load();
      cancelEdit();
    } catch (e: any) {
      setError(e?.message ?? String(e));
    }
  }

  async function remove(it: T) {
    const idValue = (it as any)[idKey as string];
    if (!confirm(`Delete ${resource} ${idValue}?`)) return;
    try {
      await apiDelete(resource, idValue);
      await load();
    } catch (e: any) {
      setError(e?.message ?? String(e));
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
            const key = (it as any)[idKey as string] ?? JSON.stringify(it);
            return (
              <tr key={String(key)}>
                {columns.map((c) => (
                  <td key={c.key}>{String((it as any)[c.key] ?? '')}</td>
                ))}
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
          <h3>{(editing as any)[idKey as string] ? 'Edit' : 'Create'}</h3>
          <div style={{ display: 'grid', gap: 8 }}>
            {columns
              .filter((c) => c.key !== idKey)
              .map((col) => {
                const key = col.key;
                return (
                  <label key={key}>
                    <div style={{ fontSize: 12 }}>{col.label}</div>
                    <input
                      value={(form as any)[key] ?? ''}
                      onChange={(e) => setForm({ ...form, [key]: e.target.value })}
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